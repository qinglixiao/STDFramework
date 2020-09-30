import 'package:flutter/material.dart';
import 'package:flutter_ienglish_fine/src/business/budget/bean/good_confirm.dart';
import 'package:flutter_lib/flutter_lib.dart';
import 'package:flutter_ienglish_fine/l10n/values.dart';
import 'package:flutter_ienglish_fine/generated/l10n.dart';
import 'package:flutter_ienglish_fine/src/config/name_router.dart';
import 'package:flutter_ienglish_fine/src/business/mine/bean/mine_buy_list.dart';
import 'package:flutter_ienglish_fine/src/business/mine/viewmodel/mine_buy_list_view_model.dart';
import 'package:flutter_ienglish_fine/src/business/mine/view/my_buy/mine_buy_list_item_widget.dart';

class PayListPage extends StatefulWidget {
  @override
  _PayListPageState createState() => _PayListPageState();
}

class _PayListPageState extends State<PayListPage> with PageBridge, AutomaticKeepAliveClientMixin {

  MineBuyListViewModel _viewModel = MineBuyListViewModel();

  SimpleLoadMoreController _loadMoreController;
  List <MineBuyListItem> _items = List();

  @override
  bool get wantKeepAlive => true;

  void loadMore() {
    _viewModel.loadPayList(isFirst: false, isRefresh: false);
  }

  @override
  Widget build(BuildContext context) {
    super.build(context);
    if (_loadMoreController == null) {
      _loadMoreController = SimpleLoadMoreController(() {
        loadMore();
      });
    }
    return RootPageWidget(
        viewModel: _viewModel,
        task: _viewModel.loadPayList(isFirst: true,isRefresh: true),
        body: StreamBuilder<MineBuyList>(
            stream: _viewModel.streamPayList,
            builder:
                (BuildContext context, AsyncSnapshot<MineBuyList> snapshot) {
              if (snapshot.data == null || snapshot.data?.items == null) {
                return CommonWidget.emptyWidget();
              }
              if(_viewModel.pageIndex == 1){
                _items.clear();
              }
              _items.addAll(snapshot.data.items);

              _loadMoreController.hasMore = _viewModel.hasMoreList;
              return PullToRefresh(
                child: SListView(_buildItemView, itemAction: _itemAction,moreController: _loadMoreController)
                    .build(context, _items),
                onRefresh: () {
                  return _viewModel.loadPayList(isFirst: false,isRefresh: true);
                },
              );
            }));
  }

  Widget _buildItemView(BuildContext context, Object itemData) {
    return BuyListItemWidget(context, itemData, (int type){
      MineBuyListItem data = itemData as MineBuyListItem;
      if(type==0){
        BuyCollocationInfo buyCollocationInfo = BuyCollocationInfo();
        buyCollocationInfo.numberCode = data.numberCode;
        buyCollocationInfo.numberDesc = data.numberDesc;
        buyCollocationInfo.payDesc = data.payDesc;
        buyCollocationInfo.payFee = data.paymentFee;
        open(RouterName.pay_affirm_page, argument: buyCollocationInfo);
      }
      else if(type==1){
        _itemAction(null, itemData);
      }
      else if(type==2){

      }
    });
  }

  void _itemAction(Widget widget, Object itemData){
    MineBuyListItem data = itemData as MineBuyListItem;
    if(data.status == S.of(context).order_status_6||data.status == S.of(context).order_status_4){
      open(RouterName.my_buy_Detail,
          argument: {'orderId': data.numberCode});
    }
    else{
      open(RouterName.receiving_detail,
          argument: {'orderId': data.numberCode});
    }
  }
}