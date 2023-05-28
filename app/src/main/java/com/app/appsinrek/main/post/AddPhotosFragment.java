package com.app.appsinrek.main.post;

import static android.app.Activity.RESULT_OK;
import static com.app.appsinrek.utils.utility.ConstantsKt.ARG_PARAM_PIX;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.FragmentAddPhotosBinding;
import com.app.appsinrek.main.MainActivity;
import com.app.appsinrek.main.post.adapters.MainImageAdapter;
import com.app.appsinrek.models.post.PostModel;
import com.app.appsinrek.player_view.enums.PostType;
import com.app.appsinrek.utils.UtilityHelperKt;
import com.app.appsinrek.viewmodels.Img;
import com.app.appsinrek.viewmodels.MainViewModel;
import com.app.appsinrek.viewmodels.Mode;
import com.app.appsinrek.viewmodels.ModelList;
import com.app.appsinrek.viewmodels.Options;
import com.app.appsinrek.viewmodels.helpers.LocalResourceManager;
import com.app.appsinrek.viewmodels.helpers.PermissionsHelperKt;
import com.app.appsinrek.viewmodels.helpers.SelectionHelperKt;
import com.app.appsinrek.viewmodels.interfaces.OnSelectionListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.ak1.pix.helpers.SystemUiHelperKt;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddPhotosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddPhotosFragment extends Fragment {
    private final int GALLERY_REQ_CODE = 1000;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ActivityResultLauncher<Intent> launcher;

    public AddPhotosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddPhotosFragment newInstance(String param1, String param2) {
        AddPhotosFragment fragment = new AddPhotosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            options = getArguments().getParcelable(ARG_PARAM_PIX);
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        } else {
            options = new Options();
            options.setSpanCount(3);
        }
        SystemUiHelperKt.getScreenSize(requireActivity());
    }

    FragmentAddPhotosBinding bi;
    private List<PostModel> postModelList = new ArrayList<>();
    private MainViewModel mainViewModel;
    private final ActivityResultLauncher<String[]> permReqLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                if (result.containsValue(false)) {
                    //one of the permissions is not granted, use result[0] to get result for individual permissions
                    UtilityHelperKt.toast(requireActivity(), getResources().getString(R.string.permission_camera_denied));
                } else {
                    //all permissions granted do something
                    mainViewModel.retrieveImages(new LocalResourceManager(requireActivity()));
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        bi = DataBindingUtil.inflate(inflater, R.layout.fragment_add_photos, container, false);
        ((AppCompatActivity) getActivity()).setSupportActionBar(bi.toolbar);
        PermissionsHelperKt.permissionsFilter(permReqLauncher, requireActivity(), options, (Function0<Unit>) () -> {
//            mainViewModel.retrieveImages(new LocalResourceManager(requireActivity()));
            LocalResourceManager localResourceManager = new LocalResourceManager(requireActivity());
            localResourceManager.setPreSelectedUrls(options.getPreSelectedUrls());
            mainViewModel.retrieveImages(localResourceManager);
            return null;
        });
        setupRecyclerView();
        observeSelectionList();
        bi.next.setOnClickListener(view -> {
            if (SelectionHelperKt.mainImageAdapter.getSelected().size() > 0) {
                Img img = SelectionHelperKt.mainImageAdapter.getItemList().get(SelectionHelperKt.mainImageAdapter.getSelected().get(0));
//                Uri inputUri = img.getContentUrl();
//                UCrop.Options options = new UCrop.Options();
//                options.setAspectRatioOptions(1,
//                        new AspectRatio("1:1", 1, 1),
//                        new AspectRatio("3:4", 3, 4),
//                        new AspectRatio("16:9", 16, 9));
//                options.setCircleDimmedLayer(true);
//                Intent intent_crop =  UCrop.of(inputUri, outputUri)
//                        .withOptions(options)
//                        .getIntent(getContext());
//                activityResultCrop.launch(intent_crop);
                switch (img.getMediaType()) {
                    case 3:
                        Intent intent = new Intent(requireActivity(), FinalStepActivity.class);
                        intent.putExtra("path", getPath(img.getContentUrl()));
                        intent.putExtra("type", PostType.VIDEO.getValue());
                        startActivity(intent);
                        break;
                    case 1:
                        Intent img_intent = new Intent(requireActivity(), PhotoCropActivity.class);
                        img_intent.putExtra("path", getPath(img.getContentUrl()));
                        img_intent.putExtra("type", PostType.IMAGE.getValue());
                        startActivity(img_intent);
                        break;
                }
                SelectionHelperKt.mainImageAdapter.getSelected().clear();
            }
        });
        bi.btnCamera.setOnClickListener(view -> {
            showBottomSheet();
        });
        bi.Gallery.setOnClickListener(view -> {
//            View overlayView = LayoutInflater.from(getContext()).inflate(R.layout.overlay_layout, null);
//            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
//            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
//            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
//            layoutParams.format = PixelFormat.TRANSLUCENT;
//            ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).addView(overlayView, layoutParams);

            Context wrapper = new ContextThemeWrapper(getContext(), R.style.FolderMenu);
            PopupWindow popupMenu = new PopupWindow(wrapper);
            View customView = LayoutInflater.from(getContext()).inflate(R.layout.folder_menus, null);
            customView.setClipToOutline(true);
            customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.folder_menu_background));
            popupMenu.setContentView(customView);
            customView.setMinimumWidth(200);
            popupMenu.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            popupMenu.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_white));
            popupMenu.setFocusable(true);
            popupMenu.showAsDropDown(bi.Gallery);
            TextView photoBtn = customView.findViewById(R.id.photo);
            TextView videoBtn = customView.findViewById(R.id.video);
            photoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent iGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    launcher.launch(iGallery);
                    popupMenu.dismiss();
                }
            });
            videoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent iGallery = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                    launcher.launch(iGallery);
                    popupMenu.dismiss();
                }
            });
//            popupMenu.setOnDismissListener(new PopupWindow.OnDismissListener() {
//                @Override
//                public void onDismiss() {
//                    // remove the overlay view from the window's decor view
//                    ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).removeView(overlayView);
//                }
//            });
//            popupMenu.getMenuInflater().inflate(R.menu.folder_menu, popupMenu.getMenu());
//            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                @Override
//                public boolean onMenuItemClick(MenuItem menuItem) {
//                    if(menuItem.getTitle().equals("Photos")) {
//
//                    }
//                    else if(menuItem.getTitle().equals("Videos")) {
//
//                    }
//                    return false;
//                }
//            });
//            popupMenu.show();
        });
        bi.mtTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        options.setMode(Mode.Picture);
                        mainViewModel.setOptions(options);
                        SelectionHelperKt.mainImageAdapter.getSelected().clear();
                        mainViewModel.getAllImagesList().postValue(new ModelList());
                        mainViewModel.retrieveImages(new LocalResourceManager(requireActivity()));
                        break;
                    case 1:
                        options.setMode(Mode.Video);
                        mainViewModel.setOptions(options);
                        SelectionHelperKt.mainImageAdapter.getSelected().clear();
                        mainViewModel.getAllImagesList().postValue(new ModelList());
                        mainViewModel.retrieveImages(new LocalResourceManager(requireActivity()));
                        break;
                    case 2:
                        Intent intent = new Intent(requireActivity(), FinalStepActivity2.class);
                        intent.putExtra("type", PostType.TEXT.getValue());
                        startActivity(intent);
                        bi.mtTabs.selectTab(bi.mtTabs.getTabAt(0));
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        ContentResolver cR = getContext().getContentResolver();
                        String type_ = cR.getType(uri);
                        type_ = type_.substring(0, type_.indexOf('/'));

//                        switch (type_) {
//                            case "video":
                        if (uri.toString().contains("video")) {
                            Intent intent = new Intent(requireActivity(), FinalStepActivity.class);
                            intent.putExtra("path", getPath(uri));
                            intent.putExtra("type", PostType.VIDEO.getValue());
                            startActivity(intent);
                        }
//                                break;
//                            case "image":
                        if (uri.toString().contains("image")) {
                            Intent img_intent = new Intent(requireActivity(), PhotoCropActivity.class);
                            img_intent.putExtra("path", getPath(uri));
                            img_intent.putExtra("type", PostType.IMAGE.getValue());
                            startActivity(img_intent);
                        }
//                                break;
//                        }

                        SelectionHelperKt.mainImageAdapter.getSelected().clear();
                    }
                }
        );
        return bi.getRoot();
    }

    /**
     * Activity result handler after image crop from both case either camera or gallery
     **/
    private final ActivityResultLauncher<Intent> activityResultCrop = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            Uri uri = UCrop.getOutput(result.getData());

        }
    });

    private final ActivityResultLauncher<Intent> activityResultPost = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
//                ((MainActivity) AddPhotosFragment.this.requireActivity()).setBottomSheet(0);
                ((MainActivity) requireActivity()).setBottomSheet(0);
            }
        }
    });
    private final ActivityResultLauncher<Intent> activityResultVideo = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            bi.mtTabs.selectTab(bi.mtTabs.getTabAt(1));
            assert result.getData() != null;
            Uri contentURI = result.getData().getData();
            String selectedVideoPath = getPath(contentURI);
            saveVideoToInternalStorage(selectedVideoPath);
            Intent intent = new Intent(requireActivity(), FinalStepActivity.class);
            intent.putExtra("type", PostType.VIDEO.getValue());
            intent.putExtra("path", selectedVideoPath);
            activityResultPost.launch(intent);
        }
    });

    private final ActivityResultLauncher<Intent> activityResultImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            bi.mtTabs.selectTab(bi.mtTabs.getTabAt(0));
            assert result.getData() != null;
            Uri uri = result.getData().getData();
            Intent intent = new Intent(requireActivity(), FinalStepActivity.class);
            intent.putExtra("type", PostType.IMAGE.getValue());
            intent.putExtra("path", uri.getPath());
            activityResultPost.launch(intent);
            Toast.makeText(requireActivity(), "Task Done", Toast.LENGTH_SHORT).show();
        } else if (result.getResultCode() == com.github.dhaval2404.imagepicker.ImagePicker.RESULT_ERROR) {
            Toast.makeText(requireActivity(), com.github.dhaval2404.imagepicker.ImagePicker.getError(result.getData()), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireActivity(), "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    });


    private void takeVideoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);
        activityResultVideo.launch(intent);
    }


    private String[] arrayOf(String s, String s1) {
        return new String[0];
    }

    private void takeImageFromCamera() {
        com.github.dhaval2404.imagepicker.ImagePicker.with(this)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .cameraOnly()
                .createIntent(intent -> {
                    activityResultImage.launch(intent);
                    return null;
                });
    }

    private void showBottomSheet() {
        final BottomSheetDialog bottomSheet = new BottomSheetDialog(requireActivity(), R.style.bottomSheetStyle);
        bottomSheet.setContentView(R.layout.fragment_bottomsheet);
        FloatingActionButton image = bottomSheet.findViewById(R.id.image);
        FloatingActionButton video = bottomSheet.findViewById(R.id.video);
        bottomSheet.show();
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet.dismiss();
                takeImageFromCamera();
            }
        });
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet.dismiss();
                takeVideoFromCamera();
            }
        });
//        final BottomSheetDialog dialog = new BottomSheetDialog(requireActivity(),R.style.bottomSheetStyle);
//        View contentView = View.inflate(getContext(), R.layout.fragment_bottomsheet, null);
//        dialog.setContentView(contentView);
//        FloatingActionButton btnClose = (FloatingActionButton) dialog.findViewById(R.id.image);
//        btnClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
//        BottomAddDeviceBinding bi = BottomAddDeviceBinding.inflate(getLayoutInflater());
//        bi.recycler.setAdapter(new BottomViewAdapter(requireActivity(), view -> {
//            int tag = (int)view.getTag();
////            if(tag==0){
////                takeImageFromCamera();
////            }else{
////                takeVideoFromCamera();
////            }
//            dialog.dismiss();
//        }));
//        dialog.show();
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    private static final String VIDEO_DIRECTORY = "/demonuts";

    private void saveVideoToInternalStorage(String filePath) {
        File newfile;
        try {
            File currentFile = new File(filePath);
            File wallpaperDirectory = new File(Environment.getExternalStorageDirectory() + VIDEO_DIRECTORY);
            newfile = new File(wallpaperDirectory, Calendar.getInstance().getTimeInMillis() + ".mp4");

            if (!wallpaperDirectory.exists()) {
                wallpaperDirectory.mkdirs();
            }

            if (currentFile.exists()) {

                InputStream in = new FileInputStream(currentFile);
                OutputStream out = new FileOutputStream(newfile);

                // Copy the bits from instream to outstream
                byte[] buf = new byte[1024];
                int len;

                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
                Log.v("vii", "Video file saved successfully.");
            } else {
                Log.v("vii", "Video saving failed. Source file missing.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = requireActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    private Options options;

    private void setupRecyclerView() {
        OnSelectionListener onSelectionListener = new OnSelectionListener() {
            @Override
            public void onLongClick(@Nullable Img element, @Nullable View view, int position) {

            }

            @Override
            public void onClick(@Nullable Img element, @Nullable View view, int position) {

            }
        };
        SelectionHelperKt.mainImageAdapter = new MainImageAdapter(requireActivity(), options.getSpanCount(), mainViewModel.getOptions());
        SelectionHelperKt.mainImageAdapter.addOnSelectionListener(onSelectionListener);
        SelectionHelperKt.mainImageAdapter.setHasStableIds(true);
        bi.recycler.setLayoutManager(new GridLayoutManager(requireActivity(), options.getSpanCount()));
        bi.recycler.setAdapter(SelectionHelperKt.mainImageAdapter);
    }

    private void observeSelectionList() {
        mainViewModel.setOptions(options);
        mainViewModel.getImageList().observe(requireActivity(), modelList -> {
            //Log.e(TAG, "imageList size is now ${it.list.size}")
            SelectionHelperKt.mainImageAdapter.setOptions(options);
            SelectionHelperKt.mainImageAdapter.getItemList().clear();
            SelectionHelperKt.mainImageAdapter.addImageList(modelList.getList());
            mainViewModel.getSelectionList().getValue().addAll(modelList.getSelection());
//            mainViewModel.getSelectionList().postValue(mainViewModel.getSelectionList().getValue());

        });
        mainViewModel.getSelectionList().observe(requireActivity(), modelList -> {
            //Log.e(TAG, "selectionList size is now ${it.size}")
            if (modelList.size() == 0) {
                mainViewModel.getLongSelection().postValue(false);
            } else if (!mainViewModel.getLongSelectionValue()) {
                mainViewModel.getLongSelection().postValue(true);
            }
        });
        mainViewModel.getCallResults().observe(requireActivity(), data -> {
            mainViewModel.getSelectionList().postValue(new HashSet());
            options.getPreSelectedUrls().clear();
            mainViewModel.getSelectionList().postValue(((Set<Img>) data.peekContent()));
        });
    }

    private String getTempDirectoryPath() {
        File cache = null;
        cache = getContext().getCacheDir();
        cache.mkdirs();
        return cache.getAbsolutePath();
    }


}