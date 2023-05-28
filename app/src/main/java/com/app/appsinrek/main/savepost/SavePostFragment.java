package com.app.appsinrek.main.savepost;

import static com.app.appsinrek.utils.AppConstants.TAG_BOOKMARK;
import static com.app.appsinrek.utils.AppConstants.TAG_SHOW_ALL_BOOKMARK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.app.appsinrek.R;
import com.app.appsinrek.custom.GridSpacingItemDecoration;
import com.app.appsinrek.databinding.FragmentSavepostBinding;
import com.app.appsinrek.main.MainActivity;
import com.app.appsinrek.main.dashboard.PostPreviewFragment;
import com.app.appsinrek.main.profile.ProfileFragment;
import com.app.appsinrek.main.profile_other.ProfileOtherFragment;
import com.app.appsinrek.main.savepost.adapter.SavePostAdapter;
import com.app.appsinrek.main.savepost.models.Bookmark;
import com.app.appsinrek.models.ResponseData;
import com.app.appsinrek.utils.UtilityHelperKt;
import com.app.appsinrek.viewmodels.MainViewModel;
import com.fitness.modval.interfaces.AuthListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SavePostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SavePostFragment extends Fragment implements AuthListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private AppCompatActivity mActivity;
    private ActivityResultLauncher<Intent> requestLauncher;

    public SavePostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SavePostFragment newInstance(String param1, String param2) {
        SavePostFragment fragment = new SavePostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        String event = data.getStringExtra("event");
                        if(event.equalsIgnoreCase("click_for_profile")){
                            String user_id = data.getStringExtra("user_id");
                            if (mainViewModel.getUser().getId().equals(UtilityHelperKt.getText(user_id))) {
                                ((MainActivity)requireActivity()).setCurrentFragment(new ProfileFragment(),"");
                                return;
                            }
                            if(UtilityHelperKt.getText(user_id).isEmpty()){
                                UtilityHelperKt.toast(requireActivity(),"User Data Empty");
                                return;
                            }
                            Fragment fragment = new ProfileOtherFragment();
                            Bundle bu = new Bundle();
                            bu.putString("userid",user_id);
                            fragment.setArguments(bu);
                            ((MainActivity)requireActivity()).setCurrentFragment(fragment,"");
                        }
                    }
                });
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
    }
    FragmentSavepostBinding bi;
    SavePostAdapter adapter;
    MainViewModel mainViewModel;
    private ArrayList<Bookmark> postModelList = new ArrayList<>();
    String userid;
    @Override
    synchronized
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        bi = DataBindingUtil.inflate(inflater, R.layout.fragment_savepost, container, false);
        mActivity = ((AppCompatActivity) getActivity());
        mActivity.setSupportActionBar(bi.toolbar);
        if (mActivity.getSupportActionBar() != null){
            mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mActivity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        bi.setMainViewModel(mainViewModel);

        mainViewModel.setMainListener(this);

        adapter = new SavePostAdapter(getActivity(), view->{
            int id  = view.getId();
            if(id == R.id.card){
                int position = (int) view.getTag();
                Gson gson = new Gson();
                String listString = gson.toJson( postModelList, new TypeToken<ArrayList<Bookmark>>() {}.getType());
                Fragment fragment = new PostPreviewFragment();
                Bundle bu = new Bundle();
                bu.putString("user_id",mainViewModel.getUser().getId());
                bu.putString("post_array",listString);
                bu.putInt("click_index",position);
                fragment.setArguments(bu);
                ((MainActivity)requireActivity()).setCurrentFragment(fragment,"");
            }else if (view.getId() == R.id.delete) {
                Bookmark model = (Bookmark) view.getTag();
                mainViewModel.bookmarkPosts(model.getPost().getId(), model.getPost().getPostBookmark()== 0?"1":"0");
            }
        },postModelList);
        int spacing = 16; // 50px
        bi.recycler.addItemDecoration(new GridSpacingItemDecoration(2,spacing,false));
        bi.recycler.setAdapter(adapter);

        return bi.getRoot();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getParentFragmentManager().popBackStack();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onStart() {
        super.onStart();
        mainViewModel.showBookmarkedPosts();
    }

    @Override
    public void onStarted(@NonNull String tag) {
        bi.progressCircular.setVisibility(View.VISIBLE);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onSuccess(@NonNull String tag, @NonNull LiveData<ResponseData> apiResponse) {
        if(tag.equals(TAG_SHOW_ALL_BOOKMARK)) {
            apiResponse.observe(this, responseData -> {
                bi.progressCircular.setVisibility(View.GONE);
                try {
                    postModelList.clear();
                    if (responseData.getCode() == 200) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<Bookmark>>() { }.getType();
                        JsonArray js = gson.toJsonTree(responseData.getMsg()).getAsJsonArray();
                        JSONArray list = new JSONArray(js.toString());
                        for (int i =0;i<list.length();i++){
                          JSONObject row = list.getJSONObject(i);
                         JSONObject Post = row.getJSONObject("Post");
                         JSONObject User = Post.getJSONObject("User");
                         row.put("User",User);
                         Post.remove("User");
                         row.put("Post",Post);
                         list.put(i, row);
                        }
                        postModelList.addAll(gson.fromJson(list.toString(), type));
                        adapter.notifyDataSetChanged();
                    } else {
//                        Toast.makeText(getActivity(), responseData.getMsg().toString(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                }

            });
        }
        else if(tag.equals(TAG_BOOKMARK)){
            apiResponse.observe(this,responseData -> {
                bi.progressCircular.setVisibility(View.GONE);
                try {
                        if (responseData.getCode() == 200) {
                            mainViewModel.showBookmarkedPosts();
                            Toast.makeText(getActivity(), "Post remove from saved list.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), responseData.getMsg().toString(), Toast.LENGTH_LONG).show();
                        }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    @Override
    public void onFailure(@NonNull String message) {
        bi.progressCircular.setVisibility(View.GONE);
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
    }
}