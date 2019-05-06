package com.helenacorp.android.mybibliotheque.Controllers.Fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.helenacorp.android.mybibliotheque.GoogleBooksApi;
import com.helenacorp.android.mybibliotheque.R;
import com.helenacorp.android.mybibliotheque.SimpleScannerActivity;
import com.helenacorp.android.mybibliotheque.model.Book;
import com.helenacorp.android.mybibliotheque.model.BookModel;
import com.helenacorp.android.mybibliotheque.service.BookLookupService;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddBookFragment extends Fragment implements View.OnClickListener, BookLookupService.Callbacks {
    private static final int REQUEST_IMAGE_CAPTURE = 111;
    private static final int ZXING_CAMERA_PERMISSION = 1;

    String result;
    private Class<?> mClss;
    private EditText titleName, lastName, category;
    private EditText isbn;
    private String isbnId;
    private Button btnClean, btnAdd, btnIsbn, btnVerif;
    private RatingBar ratingBar;
    private TextView resum;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ImageView mImageBook, mImageBookVisible;
    private Uri imguri;
    private DatabaseReference databaseReference;
    private RequestManager glide;

    public static AddBookFragment newInstance() {
        return (new AddBookFragment());
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_book, container, false);

        titleName = (EditText) view.findViewById(R.id.title_submit);
        lastName = (EditText) view.findViewById(R.id.autorLastName_submit);
        category = (EditText) view.findViewById(R.id.category_submit);
        resum = view.findViewById(R.id.submit_resum);
        btnClean = (Button) view.findViewById(R.id.btn_clean_submit);
        btnAdd = (Button) view.findViewById(R.id.btn_add_submit);
        btnVerif = view.findViewById(R.id.btn_verify_isbn_submit);
        mImageBook = (ImageView) view.findViewById(R.id.submit_photoView);
        mImageBookVisible = (ImageView) view.findViewById(R.id.submit_viewpic);
        btnIsbn = (Button) view.findViewById(R.id.submit_btn_isbn);
        isbn = view.findViewById(R.id.isbn);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        ratingBar = (RatingBar) view.findViewById(R.id.submit_rating);
        ratingBar.getNumStars();
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {

                Toast.makeText(getContext(), Float.toString(rating), Toast.LENGTH_SHORT).show();

            }

        });

        btnAdd.setOnClickListener(this);
        btnClean.setOnClickListener(this);
        btnVerif.setOnClickListener(this);
        btnIsbn.setOnClickListener(this);
        mImageBook.setOnClickListener(this);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                imguri = data.getData();
                try {
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), imguri);
                    // mImageBook.setVisibility(View.INVISIBLE);
                    mImageBookVisible.setMaxWidth(78);
                    mImageBookVisible.setMaxHeight(78);
                    mImageBookVisible.setAdjustViewBounds(true);
                    mImageBookVisible.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    mImageBookVisible.setImageBitmap(imageBitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String returnValue = data.getStringExtra("barcode");
                isbn.setText(returnValue);
            }
        }
    }

    //validate label from autor and title
    public void validation() {
        if (titleName.getText().length() == 0 || lastName.length() == 0 || validationIsbn()) {
            if (titleName.getText().length() == 0 || lastName.length() == 0) {
                Context context = getContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, R.string.toast_2, duration);
                toast.show();
            }
        } else {
            if (mImageBookVisible != null) sendBookcover();
        }
    }

    //if isbn of book exist
    public boolean validationIsbn() {
        String isbnId = isbn.getText().toString();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("books");
        Query isbnQuery = ref.orderByChild("isbn").equalTo(isbnId);
        isbnQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(getContext(), R.string.toast, Toast.LENGTH_SHORT).show();
                } else {
                    if (mImageBookVisible != null) sendBookcover();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return true;
    }

    //compresse la photo sélectionnée pour la couverture et l'envoie sur firebase
    public void sendBookcover() {
        final String userName = user.getDisplayName();
        mImageBookVisible.setDrawingCacheEnabled(true);
        mImageBookVisible.buildDrawingCache();
        Bitmap bitmap = mImageBookVisible.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("books").push();

        final StorageReference userPic = storageReference.child("couvertures/" + user.getUid() + titleName.getText().toString().trim() + ".jpg");
        byte[] data = baos.toByteArray();
        final UploadTask uploadTask = userPic.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String imageDownloadUrl = userPic.getDownloadUrl().toString();
                BookModel bookModel = new BookModel(titleName.getText().toString(), category.getText().toString(), isbn.getText().toString(),
                        lastName.getText().toString(), userName, null, ratingBar.getRating(), userPic.getDownloadUrl().toString(), resum.getText().toString());
                //Save image info in to firebase database
                //keys = name's attribut
                Map<String, Object> map = new HashMap<>();
                map.put("title", bookModel.getTitle());
                map.put("isbn", bookModel.getIsbn());
                map.put("lastnameAutor", bookModel.getLastnameAutor());
                map.put("rating", bookModel.getRating());
                map.put("imageUrl", bookModel.getImageUrl());
                map.put("category", bookModel.getCategory());
                map.put("info", bookModel.getInfo());
                map.put("bookid", databaseReference.getKey());
                databaseReference.setValue(map);
                Toast.makeText(getContext(), "Enregistré!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void clearEditText(EditText editText) {
        editText.setText("");
    }

    public void cleanCouv(ImageView img) {
        if (img != null) {
            img.setImageResource(R.drawable.clean_image_submit);
            img.setMaxWidth(80);
            img.setMaxHeight(80);
            img.setAdjustViewBounds(true);
            img.setScaleType(ImageView.ScaleType.CENTER);
            // img.getDrawingCache(false);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View view) {
        if (view == btnVerif) {
            //  new FetchBookTask().execute(getISBN());
            this.executeHttpRequestWithRetrofit();
        }
        if (view == btnAdd) {
            validation();
        }
        if (view == btnClean) {
            clearEditText(titleName);
            clearEditText(lastName);
            clearEditText(isbn);
            clearEditText(category);
            resum.setText("");
            cleanCouv(mImageBookVisible);
        }
        if (view == mImageBook) {
            shooseToCamera();
        }
        if (view == btnIsbn) {
            launchActivity(SimpleScannerActivity.class);
        }
    }

    public void shooseToCamera() {
        Intent intent = new Intent();
        //change intent.setType("images/*") by ("*/*")
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_CAPTURE);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void launchActivity(Class<?> clss) {
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(this.getContext()), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            mClss = clss;
            ActivityCompat.requestPermissions(Objects.requireNonNull(this.getActivity()),
                    new String[]{Manifest.permission.CAMERA}, ZXING_CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(getContext(), clss);
            startActivityForResult(intent, 1);
        }
    }

    //ouvre l'activité du scan code bar
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case ZXING_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mClss != null) {
                        Intent intent = new Intent(getContext(), mClss);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(getContext(), "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String getISBN() {
        final EditText isbnField = (EditText) Objects.requireNonNull(getActivity()).findViewById(R.id.isbn);
        return isbnField.getText().toString();
    }

    private void showMessage(String message) {
        Toast.makeText(this.getContext().getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void executeHttpRequestWithRetrofit() {
        // this.updateUIWhenStartingHTTPRequest();
        BookLookupService.fetchBookByISBN(this, getISBN());
        Log.e("AddBookFragment", "getISBN from editText : => " + getISBN());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReponse(Book book) {
        if(!getISBN().isEmpty() || book.getTotalItems() != 0){
            Log.e("AddBookFragment ", "apres book != null " + "TotalItems : "+ book.getTotalItems().toString());
            if(book.getItems().get(0).getVolumeInfo().getIndustryIdentifiers().get(1).getIdentifier().equals(getISBN())){

                if (!book.getItems().get(0).getVolumeInfo().getTitle().isEmpty() || !book.getItems().get(0).getVolumeInfo().getAuthors().isEmpty()) {

                  //  Log.e("AddBookFragment", " totalItems = "+ book.getTotalItems()+" ISBN get : "+book.getItems().get(0).getVolumeInfo().getIndustryIdentifiers().get(0).getIdentifier());
                    titleName.setText(book.getItems().get(0).getVolumeInfo().getTitle());
                    lastName.setText(book.getItems().get(0).getVolumeInfo().getAuthors().get(0).toString());

                    if (book.getItems().get(0).getVolumeInfo().getDescription() != null) {
                        resum.setText(book.getItems().get(0).getVolumeInfo().getDescription().toString());
                        if (book.getItems().get(0).getVolumeInfo().getImageLinks() != null) {
                            Glide.with(Objects.requireNonNull(getContext())).load(book.getItems().get(0).getVolumeInfo().getImageLinks().getThumbnail()).apply(RequestOptions.circleCropTransform()).into(mImageBookVisible);
                            Log.e("AddBookFragment ", "img => " + book.getItems().get(0).getVolumeInfo().getImageLinks().getThumbnail());
                        }
                    }
                }
        }else {
                Toast.makeText(getContext(), "Pas de livres dans la base ", Toast.LENGTH_LONG).show();
            }

        }else{
            Toast.makeText(getContext(), " isbn non renseigné ", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onFailure() {
        Log.e("AddBookFragment ", " failure");
                Toast.makeText(getContext(), "failed", Toast.LENGTH_LONG).show();
    }

    //UPDATE UI
    private void updateUIWhenStartingHTTPRequest() {

    }

    private void updateUIWithListOfUsers(BookModel bookModel) {
        StringBuilder stringBuilder = new StringBuilder();

        updateUIWhenStopingHTTPRequest(stringBuilder.toString());
    }

    private void updateUIWhenStopingHTTPRequest(String response) {
        this.isbn.setText(response);
    }


    //récupère/extrait les infos sur google books
    /*class FetchBookTask extends AsyncTask<String, Void, BookModel> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("recherche dans la base de données");
            progressDialog.show();
        }

        @Override
        protected BookModel doInBackground(String... params) {
            final String isbn = params[0];
            try {
                return new BookLookupService().fetchBookByISBN(isbn);
            } catch (Exception e) {
                Log.e("fetchBookByISBN", e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(BookModel book) {
            try {
                if (book != null) {
                    //showMessage("Got book: " + book.getTitle());
                    titleName.setText(book.getTitle());
                    if (book.getDescription() != null || book.getAuthors() != null || book.getImageLinks().getThumbnail() != null) {
                        lastName.setText(book.getAuthors().get(0).toString());
                        resum.setText(book.getDescription().toString());
                        mImageBookVisible.setAdjustViewBounds(true);
                        Picasso.get().load(book.getImageLinks().getThumbnail())
                                //.load("http://books.google.com/books/content?id=r6hhDwAAQBAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api")
                                .placeholder(R.drawable.ic_user)
                               //.error(R.drawable.acc_profil)
                                .resize(48,48)
                                .into(mImageBookVisible);
                    }
                } else {
                    showMessage("Ce livre n'est pas dans la base de données");
                }
            } catch (Exception e) {
                Log.e("Tag", "====>pas d\'image");
            }
            progressDialog.dismiss();
        }
    }

*/

}
