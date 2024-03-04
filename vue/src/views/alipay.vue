<template>
  <div>
    <!-- 商品界面 -->
    <!--主体的表格-->
    <el-table :data="tableData" border stripe header-cell-class-name="headerBg"
              @selection-change="handleSelectionChange">
      <el-table-column
          type="selection"
          width="55">
      </el-table-column>
      <el-table-column prop="pageGoods.id" label="ID" width="80" align="center">
      </el-table-column>
      <el-table-column prop="pageGoods.goodName" label="商品名称" width="140" align="center">
      </el-table-column>
      <el-table-column prop="pageGoods.goodId" label="商品编号" width="120" align="center">
      </el-table-column>
      <el-table-column prop="pageGoods.goodPrice" label="商品价格" width="120" align="center">
      </el-table-column>
      <el-table-column prop="pageGoods.createTime" label="创建时间" width="200" align="center">
      </el-table-column>
      <el-table-column label="操作" width="200" align="center">
        <template slot-scope="scope">

          <!-- 购买按钮确认框 -->
          <el-popconfirm
              class="ml-5"
              confirm-button-text='确定'
              cancel-button-text='不用了'
              icon="el-icon-info"
              icon-color="red"
              title="您确定购买吗？"
              @confirm="buy(scope.row.id)"
          >
            <!-- 删除按钮-->
            <el-button type="danger" slot="reference">购买
            </el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <div class="block">
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-sizes="[5, 10, 15, 20]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
      </el-pagination>
    </div>

    <!-- 订单界面 -->

    <!--主体的表格-->
    <el-table :data="order_tableData" border stripe header-cell-class-name="headerBg"
              @selection-change="handleSelectionChange">
      <el-table-column
          type="selection"
          width="55">
      </el-table-column>
      <el-table-column prop="id" label="ID" width="80" align="center">
      </el-table-column>
      <el-table-column prop="orderNo" label="订单编号" width="120" align="center">
      </el-table-column>
      <el-table-column prop="goodsName" label="商品名称" width="140" align="center">
      </el-table-column>
      <el-table-column prop="num" label="商品数量" width="140" align="center">
      </el-table-column>
      <el-table-column prop="total" label="总价格" width="120" align="center">
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="200" align="center">
      </el-table-column>
      <el-table-column prop="payNo" label="支付编号" width="200" align="center">
      </el-table-column>
      <el-table-column prop="payTime" label="支付时间" width="200" align="center">
      </el-table-column>
      <el-table-column prop="status" label="订单状态" width="200" align="center">
      </el-table-column>
      <el-table-column label="操作" width="200" align="center">
        <template slot-scope="scope">

          <!-- 购买按钮确认框 -->
          <el-popconfirm
              class="ml-5"
              confirm-button-text='确定'
              cancel-button-text='不用了'
              icon="el-icon-info"
              icon-color="red"
              title="您确定支付吗？"
              @confirm="pay(scope.row)"
          >
            <!-- 删除按钮-->
            <el-button type="success" slot="reference">支付
            </el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <div class="block">
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="order_pageNum"
          :page-sizes="[5, 10, 15, 20]"
          :page-size="order_pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="order_total">
      </el-pagination>
    </div>

  </div>
</template>

<script>
import {Message} from "element-ui";

export default {
  name: "alipay",
  data() {
    return {
      //商品表
      tableData: [],
      //查询的页数和每页查询的个数
      pageNum: 1,
      pageSize: 10,
      //商品总数
      total: 0,

      //订单表
      order_tableData: [],
      //查询的页数和每页查询的个数
      order_pageNum: 1,
      order_pageSize: 10,
      //商品总数
      order_total: 0,

    }
  },
  //页面刚刚加载完执行的代码，相当于微信小程序里面的onLoad方法
  created() {
    this.selectAll();
  },
  methods: {
    selectAll() {
      //请求分页查询数据
      //旧写法
      // axios.get("http://localhost:9090/user/page?pageNum="+this.pageNum+"&pageSize="+this.pageSize).then(res=>{
      //   console.log(res);
      //   this.tableData = res.data.records;
      //   this.total = res.data.total;
      // })

      //请求数据库中的全部商品信息
      this.request.get("/goods/page/selectAll", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize
        }
      }).then(res => {
        if (res.code === '401') {
          Message({
            message: res.msg,
            type: 'error'
          })
        }
        console.log(res);
        this.tableData = res.data;
        this.total = res.data.length;
      })
      //请求数据库中的全部订单信息
      this.request.get("/orders/page", {
        params: {
          pageNum: this.order_pageNum,
          pageSize: this.order_pageSize
        }
      }).then(res => {
        if (res.code === '401') {
          Message({
            message: res.msg,
            type: 'error'
          })
        }
        console.log(res);
        this.order_tableData = res.data.records;
        this.order_total = res.data.total;
      })

    },
    //删除按钮
    buy(id) {
      this.request.get("/orders/save", {
        params: {
          id: id
        }
      }).then(res => {
        if (res.code === '200') {
          this.$message.success("购买成功");
          this.selectAll();
        } else {
          this.$message.error("购买失败，请重新购买");
        }
      })
    },
    //每页展示的条数
    handleSizeChange(val) {
      this.pageSize = val;
      this.selectAll();
    },
    //表格前面的checkbox按钮事件
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    //当前选择的是第几页
    handleCurrentChange(val) {
      this.pageNum = val;
      this.selectAll();
    },
    //支付按钮
    pay(row){
      window.open('http://localhost:9090/alipay/pay?orderNo='+row.orderNo);

    }
  }
}
</script>

<style scoped>

</style>