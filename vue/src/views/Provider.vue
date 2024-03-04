<template>
  <div>
    <!--搜索区域-->
    <div style="margin: 10px 0">
      <el-input style="width: 200px" prefix-icon="el-icon-search" placeholder="请输入商品名称" v-model="good_name"></el-input>
      <el-input style="width: 200px;padding-left: 5px" prefix-icon="el-icon-price-tag" placeholder="请输入价格"
                v-model="good_price"></el-input>
      <el-input style="width: 200px;padding-left: 5px" prefix-icon="el-icon-position"
                placeholder="请输入类型" v-model="good_kind"></el-input>
      <el-button type="primary" class="ml-5" @click="select">搜索</el-button>
      <el-button type="warning" class="ml-5" @click="chongzhi">重置</el-button>
    </div>
    <!--操作按钮 -->
    <div style="margin: 10px 0">

      <el-button type="primary" @click="dialogVisible = true"><i class="el-icon-circle-plus" style="padding-right: 5px"></i>新增
      </el-button>
      <!-- 删除按钮确认框 -->
      <el-popconfirm
          class="ml-5"
          confirm-button-text='确定'
          cancel-button-text='不用了'
          icon="el-icon-info"
          icon-color="red"
          title="您确定批量删除这些数据吗？"
          @confirm="delBatch"
      >
        <el-button type="danger" slot="reference"><i class="el-icon-remove-outline" style="padding-right: 5px"></i>批量删除</el-button>
      </el-popconfirm>
      <el-button type="primary" class="ml-5" @click="exp"><i class="el-icon-upload2" style="padding-right: 5px"></i>导出</el-button>
    </div>
    <!--主体的表格-->
    <el-table :data="tableData" border stripe header-cell-class-name="headerBg"  @selection-change="handleSelectionChange">
      <el-table-column
          type="selection"
          width="55">
      </el-table-column>
      <el-table-column prop="goodName" label="商品名称" width="200" align="center">
      </el-table-column>
      <el-table-column prop="goodId" label="商品编号" width="500" align="center">
      </el-table-column>
      <el-table-column prop="goodPrice" label="商品价格" width="140" align="center">
      </el-table-column>
      <el-table-column prop="goodKind" label="商品类型" width="300" align="center">
      </el-table-column>
      <el-table-column prop="create_time" label="创建时间" width="200" align="center">
      </el-table-column>
      <el-table-column label="操作" width="275" align="center">
        <template slot-scope="scope">
          <!--              编辑按钮-->
          <el-button type="success" @click="handleEdit(scope.row)"><i class="el-icon-edit" style="padding-right: 5px"></i>编辑</el-button>

          <!-- 删除按钮确认框 -->
          <el-popconfirm
              class="ml-5"
              confirm-button-text='确定'
              cancel-button-text='不用了'
              icon="el-icon-info"
              icon-color="red"
              title="您确定删除吗？"
              @confirm="del(scope.row.id)"
          >
            <!-- 删除按钮-->
            <el-button type="danger" slot="reference"><i class="el-icon-delete" style="padding-right: 5px"></i>删除</el-button>
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

    <!--添加数据对话框表单-->
    <el-dialog
        title="编辑商品信息"
        :visible.sync="dialogVisible"
        width="30%"
    >
      <el-form ref="form" :model="addGood" label-width="80px">
        <el-form-item label="商品名称">
          <el-input v-model="addGood.goodName"></el-input>
        </el-form-item>
        <el-form-item label="商品价格">
          <el-input v-model="addGood.goodPrice"></el-input>
        </el-form-item>

        <el-form-item label="商品类型">
          <el-input v-model="addGood.goodKind"></el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="onSubmit">提交</el-button>
          <el-button @click="cannel">取消</el-button>
        </el-form-item>

      </el-form>

    </el-dialog>
  </div>
</template>

<script>
import {Message} from "element-ui";

export default {
  name: "Provider",
  data(){
    return{
      //模糊查询时的字段
      good_name: '',
      good_price: '',
      good_kind: '',
      //商品表
      tableData: [],
      //查询的页数和每页查询的个数
      pageNum: 1,
      pageSize: 10,
      //商品总数
      total: 0,
      addGood: {
        id:'',
        goodName:'',
        goodId:'',
        goodPrice:'',
        provideId:'',
        goodKind:'',
        username:'',
      },
      //是否显示新增表单
      dialogVisible: false,
      username : localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")).username : "",
      role : localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")).role : ""
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
      this.request.get("/goods/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          username: this.username,
          role:this.role
        }
      }).then(res => {
        if (res.code === '401') {
          Message({
            message: res.msg,
            type: 'error'
          })
        }
        console.log(res);
        this.tableData = res.data.records;
        this.total = res.data.total;
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
    //编辑按钮
    handleEdit(row) {
      this.addGood = row;
      this.dialogVisible = true;
    },
    //删除按钮
    del(id) {
      this.request.delete("/goods/" + id).then(res => {
        if (res.code==='200') {
          this.$message.success("删除成功");
          this.selectAll();
        } else {
          this.$message.error("删除失败");
        }
      })
    },
    cannel(){
      this.addGood = {};
      this.dialogVisible = false;
      this.selectAll();
    },
    //表单提交按钮
    onSubmit() {
      this.addGood.username=this.username;
      this.request.post("/goods/save", this.addGood).then(res => {
        if (res.code==='200') {
          this.$message.success("提交成功");
          this.dialogVisible = false; //隐藏对话框
          this.selectAll();
          this.addGood = {};
        } else {
          this.$message.error("提交失败，请重新提交");
        }
      })
    },
    //批量删除按钮
    delBatch() {
      let ids = this.multipleSelection.map(v => v.id) //[{},{},{},...] => [1,2,3]
      this.request.post("/goods/del/batch", ids).then(res => {
        if (res.code==='200') {
          this.$message.success("批量删除成功");
          this.selectAll();
        } else {
          this.$message.error("批量删除失败");
        }
      })
    },
    //重置按钮
    chongzhi() {
      this.good_name = '';
      this.good_price = '';
      this.good_kind = '';
      this.selectAll();
    },
    //模糊查询的搜索按钮
    select() {
      this.request.get("/goods/page/obscure/ById", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          goodName:this.good_name,
          goodPrice:this.good_price,
          goodKind:this.good_kind,
          username:this.username
        }
      }).then(res => {
        this.tableData = res.data.records;
        this.total = res.data.total;
      })
    },
    //导出按钮
    exp(){
      window.open("http://localhost:9090/goods/export?username="+this.username);
    },
  }
}

</script>

<style scoped>

</style>