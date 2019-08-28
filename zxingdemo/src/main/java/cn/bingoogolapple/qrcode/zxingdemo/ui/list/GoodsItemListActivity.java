package cn.bingoogolapple.qrcode.zxingdemo.ui.list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import org.apache.commons.lang3.StringUtils;

import cn.bingoogolapple.qrcode.zxingdemo.DTO.GoodsDTO;
import cn.bingoogolapple.qrcode.zxingdemo.ui.common.GoodsActivity;
import cn.bingoogolapple.qrcode.zxingdemo.MyApplication;
import cn.bingoogolapple.qrcode.zxingdemo.R;
import cn.bingoogolapple.qrcode.zxingdemo.utils.FileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.bingoogolapple.qrcode.zxingdemo.constant.CommonConstant.SEARCH_GOODS_ID_OR_NAME;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class GoodsItemListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    public static final int VIEW_TYPE = -1;

    public static void actionStart(Context context, String goodsIdOrName) {
        Intent intent = new Intent(context, GoodsItemListActivity.class);
        intent.putExtra(SEARCH_GOODS_ID_OR_NAME, goodsIdOrName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_goods_item_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setItemViewCacheSize(200);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        String goodsIdOrName = getIntent().getStringExtra(SEARCH_GOODS_ID_OR_NAME);
        boolean filter = true;
        if (StringUtils.isBlank(goodsIdOrName)) {
            filter = false;
        }
        MyApplication app = (MyApplication) this.getApplication();
        List<GoodsDTO> items = new ArrayList<>(app.goodsMap.size());
        GoodsDTO goods;
        for (Map.Entry<String, GoodsDTO> entry : app.goodsMap.entrySet()) {
            goods = entry.getValue();
            if (filter) {
                if (StringUtils.containsIgnoreCase(goods.getId(), goodsIdOrName)) {
                    items.add(goods);
                } else if (StringUtils.containsIgnoreCase(goods.getName(), goodsIdOrName)) {
                    items.add(goods);
                }
            } else {
                items.add(goods);
            }
        }
        recyclerView.setAdapter(new GoodsItemRecyclerViewAdapter(this, items, mTwoPane));
    }

    public static class GoodsItemRecyclerViewAdapter
            extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final GoodsItemListActivity mParentActivity;
        private final List<GoodsDTO> mGoodsList;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoodsDTO item = (GoodsDTO) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(GoodsItemDetailFragment.ARG_ITEM_ID, item.getId());
                    GoodsItemDetailFragment fragment = new GoodsItemDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    GoodsActivity.actionStart(context, item.getId());
//                    Intent intent = new Intent(context, ItemDetailActivity.class);
//                    intent.putExtra(GoodsItemDetailFragment.ARG_ITEM_ID, item.getId());
//
//                    context.startActivity(intent);
                }
            }
        };

        GoodsItemRecyclerViewAdapter(GoodsItemListActivity parent,
                                     List<GoodsDTO> items,
                                     boolean twoPane) {
            mGoodsList = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //在这里根据不同的viewType进行引入不同的布局
            if (viewType == VIEW_TYPE) {
                View emptyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_empty, parent, false);
                return new RecyclerView.ViewHolder(emptyView) {};
            }
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.goods_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public int getItemViewType(int position) {
            if (mGoodsList.size() == 0) {
                return VIEW_TYPE;
            }
            return super.getItemViewType(position);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ViewHolder) {
                ViewHolder myHolder = (ViewHolder) holder;
                String mCurrentImagePath = mGoodsList.get(position).getImagePath();
                if (StringUtils.isNotBlank(mCurrentImagePath)) {
                    FileUtil.setImage4ViewBitmap(myHolder.mGoodsImageView, mCurrentImagePath);
                }

                myHolder.mGoodsIdView.setText(mGoodsList.get(position).getId());
                myHolder.mGoodsShortView.setText(GoodsDTO.getShortID(mGoodsList.get(position).getId()));
                myHolder.mGoodsNameView.setText(mGoodsList.get(position).getName());

                myHolder.itemView.setTag(mGoodsList.get(position));
                myHolder.itemView.setOnClickListener(mOnClickListener);
            }
        }

        @Override
        public int getItemCount() {
            if (mGoodsList.size() == 0) {
                return 1;
            }
            return mGoodsList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final ImageView mGoodsImageView;
            final TextView mGoodsIdView;
            final TextView mGoodsShortView;
            final TextView mGoodsNameView;

            ViewHolder(View view) {
                super(view);
                mGoodsImageView = (ImageView)view.findViewById(R.id.goodsListImage);
                mGoodsIdView = (TextView) view.findViewById(R.id.goodsListID);
                mGoodsShortView = (TextView) view.findViewById(R.id.goodsListShortID);
                mGoodsNameView = (TextView) view.findViewById(R.id.goodsListName);
            }
        }

    }
}
