package com.example.supnumarchive;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.supnumarchive.Databases.FirebaseProfileManager;
import com.example.supnumarchive.Models.UserProfile;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditProfileDialog extends DialogFragment {

    private static final int PICK_IMAGE_REQUEST = 1001;

    private ImageView profileImage;
    private TextInputEditText nameEditText, emailEditText;
    private Button changePhotoBtn, cancelButton, saveButton;
    private FirebaseProfileManager profileManager;
    private OnProfileUpdatedListener listener;

    public interface OnProfileUpdatedListener {
        void onProfileUpdated();
    }

    public EditProfileDialog(OnProfileUpdatedListener listener) {
        this.listener = listener;
        this.profileManager = new FirebaseProfileManager();
    }

    public EditProfileDialog() {
        // Required empty constructor for DialogFragment
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_edit_profile_dialog, container, false);

        profileImage = view.findViewById(R.id.profile_image);
        nameEditText = view.findViewById(R.id.name_edit_text);
        emailEditText = view.findViewById(R.id.email_edit_text);
        changePhotoBtn = view.findViewById(R.id.change_photo_btn);
        cancelButton = view.findViewById(R.id.cancel_button);
        saveButton = view.findViewById(R.id.save_button);

        loadCurrentProfile();

        changePhotoBtn.setOnClickListener(v -> openImagePicker());
        cancelButton.setOnClickListener(v -> dismiss());
        saveButton.setOnClickListener(v -> updateProfile());

        return view;
    }

    private void loadCurrentProfile() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            nameEditText.setText(currentUser.getDisplayName());
            emailEditText.setText(currentUser.getEmail());

            profileManager.getProfileImageUrl().addOnSuccessListener(uri -> {
                Glide.with(requireContext())
                        .load(uri)
                        .circleCrop()
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .into(profileImage);
            }).addOnFailureListener(e -> {
                Toast.makeText(requireContext(), "Failed to load profile image", Toast.LENGTH_SHORT).show();
            });

            profileManager.getUserProfile().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    UserProfile userProfile = documentSnapshot.toObject(UserProfile.class);
                    if (userProfile != null && userProfile.getName() != null) {
                        nameEditText.setText(userProfile.getName());
                    }
                }
            });
        }
    }

    private void updateProfile() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Name is required");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }

        UserProfile userProfile = new UserProfile(currentUser.getUid(), name, email);

        profileManager.updateUserProfile(userProfile)
                .addOnSuccessListener(aVoid -> {
                    currentUser.updateEmail(email)
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                                if (listener != null) listener.onProfileUpdated();
                                dismiss();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(requireContext(), "Profile updated but failed to update email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                if (listener != null) listener.onProfileUpdated();
                                dismiss();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            handleImageResult(data.getData());
        }
    }

    private void handleImageResult(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] imageData = baos.toByteArray();

            profileManager.uploadProfileImage(imageData)
                    .addOnSuccessListener(taskSnapshot -> {
                        Glide.with(requireContext())
                                .load(imageUri)
                                .circleCrop()
                                .into(profileImage);
                        Toast.makeText(requireContext(), "Profile image updated", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Failed to process image", Toast.LENGTH_SHORT).show();
        }
    }
}
