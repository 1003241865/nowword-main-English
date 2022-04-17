package com.example.now_word;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dao.WordRecordDao;

import java.util.ArrayList;

import it.moondroid.coverflow.components.ui.containers.FeatureCoverFlow;

//复习页面
public class ReviewFragment extends Fragment {
    public FeatureCoverFlow featureCoverFlow;
    public CoverFlowAdapter coverFlowAdapter;
    private ArrayList<ReviewCardEntity> mData = new ArrayList<>(0);
    private WordRecordDao wordRecordDao;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.review_fragment,null);
        featureCoverFlow=view.findViewById(R.id.review_pager);
        wordRecordDao=new WordRecordDao(getContext());

        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        mData.add(new ReviewCardEntity(R.drawable.review_fox,R.string.review_finish,wordRecordDao.getTypeFinishWordCount()));
        mData.add(new ReviewCardEntity(R.drawable.review_bear,R.string.review_flag,wordRecordDao.getTypeFlagWordCount()));
        mData.add(new ReviewCardEntity(R.drawable.review_bird,R.string.review_wrong,wordRecordDao.getTypeHighWrongWordCount(3)));
        mData.add(new ReviewCardEntity(R.drawable.review_birds,R.string.review_need_review,wordRecordDao.getTypeReWordCount()));
        mData.add(new ReviewCardEntity(R.drawable.review_dog,R.string.review_finish_dicate,wordRecordDao.getTypeFinishWordCount()));
        mData.add(new ReviewCardEntity(R.drawable.review_rabbit,R.string.review_flag_dicate,wordRecordDao.getTypeFlagWordCount()));
        mData.add(new ReviewCardEntity(R.drawable.review_lion,R.string.review_wrong_dicate,wordRecordDao.getTypeHighWrongWordCount(3)));
        coverFlowAdapter=new CoverFlowAdapter(getContext());
        coverFlowAdapter.setData(mData);

        featureCoverFlow.setAdapter(coverFlowAdapter);
        featureCoverFlow.setOnItemClickListener(new AdapterView.OnItemClickListener(){


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (mData.get(position).titleResId) {
                    case R.string.review_finish:
                        intent=new Intent(getContext(),WordStudyActivity.class);
                        intent.putExtra("studyWords","finish");
                        startActivity(intent);
                        break;
                    case R.string.review_flag:
                        intent=new Intent(getContext(),WordStudyActivity.class);
                        intent.putExtra("studyWords","flag");
                        startActivity(intent);
                        break;
                    case R.string.review_wrong:
                        intent=new Intent(getContext(),WordStudyActivity.class);
                        intent.putExtra("studyWords","danger");
                        startActivity(intent);
                        break;
                    case R.string.review_need_review:
                        intent=new Intent(getContext(),WordStudyActivity.class);
                        intent.putExtra("studyWords","need_review");
                        startActivity(intent);
                        break;
                    case R.string.review_finish_dicate:
                        intent=new Intent(getContext(),DictateWordStudyActivity.class);
                        intent.putExtra("studyWords","finish");
                        startActivity(intent);
                        break;
                    case R.string.review_flag_dicate:
                        intent=new Intent(getContext(),DictateWordStudyActivity.class);
                        intent.putExtra("studyWords","flag");
                        startActivity(intent);
                        break;
                    case R.string.review_wrong_dicate:
                        intent=new Intent(getContext(),DictateWordStudyActivity.class);
                        intent.putExtra("studyWords","danger");
                        startActivity(intent);
                        break;


                }
            }
        });




        super.onActivityCreated(savedInstanceState);
    }

}
