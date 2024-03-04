<template>
  <div>
    <!--搜索区域-->
    <div style="margin: 10px 0">
      <el-input style="width: 200px" prefix-icon="el-icon-search" placeholder="请输入名称" v-model="name"></el-input>
      <el-button type="primary" class="ml-5" @click="select">搜索</el-button>
      <el-button type="warning" class="ml-5" @click="chongzhi">重置</el-button>
    </div>
    <!--操作按钮 -->
    <div style="margin: 10px 0">

      <el-button type="primary" @click="dialogVisible = true"><i class="el-icon-circle-plus"
                                                                 style="padding-right: 5px"></i>新增
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
        <el-button type="danger" slot="reference"><i class="el-icon-remove-outline" style="padding-right: 5px"></i>批量删除
        </el-button>
      </el-popconfirm>
      <!--    <el-upload action="http://localhost:9090/user/import" :show-file-list="false" accept="xlsx" :on-success="handleExcelSuccess" style="display: inline-block">-->
      <!--    <el-button type="primary" class="ml-5"><i class="el-icon-download" style="padding-right: 5px"></i>导入</el-button>-->
      <!--    </el-upload>-->
      <!--    <el-button type="primary" class="ml-5" @click="exp"><i class="el-icon-upload2" style="padding-right: 5px"></i>导出</el-button>-->
    </div>
    <!--主体的表格-->
    <el-table :data="tableData" border stripe header-cell-class-name="headerBg"
              @selection-change="handleSelectionChange">
      <el-table-column
          type="selection"
          width="55">
      </el-table-column>
      <el-table-column prop="id" label="ID" width="200" align="center">
      </el-table-column>
      <el-table-column prop="name" label="名称" align="center">
      </el-table-column>
      <el-table-column prop="flag" label="唯一标识" align="center">
      </el-table-column>
      <el-table-column prop="description" label="描述" align="center">
      </el-table-column>
      <el-table-column label="操作" width="280" align="center">
        <template slot-scope="scope">
          <!-- 分配菜单按钮 -->
          <el-button type="info" @click="selectMenu(scope.row)"><i class="el-icon-menu"
                                                                      style="padding-right: 5px"></i>分配菜单
          </el-button>
          <!-- 编辑按钮-->
          <el-button type="success" @click="handleEdit(scope.row)"><i class="el-icon-edit"
                                                                      style="padding-right: 5px"></i>编辑
          </el-button>

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
            <el-button type="danger" slot="reference"><i class="el-icon-delete" style="padding-right: 5px"></i>删除
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

    <!--添加数据对话框表单-->
    <el-dialog
        title="角色信息"
        :visible.sync="dialogVisible"
        width="30%"
    >
      <el-form ref="form" :model="form" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="唯一标识">
          <el-input v-model="form.flag"></el-input>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description"></el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="onSubmit">提交</el-button>
          <el-button @click="dialogVisible = false">取消</el-button>
        </el-form-item>

      </el-form>

    </el-dialog>

    <!-- 分配菜单对话框 -->
    <el-dialog
        title="菜单分配"
        :visible.sync="menuDialogVis"
        width="30%"
    >
      <el-form ref="form" :model="form" label-width="80px">
        <el-tree
            :props="props"
            :data="MenuData"
            show-checkbox
            ref="tree"
            node-key="id"
            :default-expanded-keys="expands"
            :default-checked-keys="checks"
        >
          <span class="custom-tree-node" slot-scope="{ node, data }">
          <span><i :class="data.icon"></i>{{ data.name }}</span>
            </span>
        </el-tree>

        <el-form-item>
          <el-button type="primary" @click="saveRoleMenu">提交</el-button>
          <el-button @click="menuDialogVis = false">取消</el-button>
        </el-form-item>

      </el-form>

    </el-dialog>
  </div>
</template>

<script>
import {Message} from "element-ui";

export default {
  name: "Role",
  data() {
    return {
      //模糊查询时的字段
      name: '',
      //查询的页数和每页查询的个数
      pageNum: 1,
      pageSize: 10,
      //用户表
      tableData: [],
      //用户总人数
      total: 0,
      //是否显示新增表单
      dialogVisible: false,
      //是否显示分配菜单表单
      menuDialogVis: false,
      form: {
        name: '',
        description: ''
      },
      //用来装批量删除选择的项
      multipleSelection: [],
      headBg: 'headBg',
      //菜单树形控件的数据
      MenuData: [],
      props:{label: 'name'},
      expands:[],
      checks:[],
      roleId:0,
      roleFlag:''
  }
}
,
//页面刚刚加载完执行的代码，相当于微信小程序里面的onLoad方法
created()
{
  this.selectAll();
}
,
methods: {
  selectAll()
  {
    //请求分页查询数据
    //旧写法
    // axios.get("http://localhost:9090/user/page?pageNum="+this.pageNum+"&pageSize="+this.pageSize).then(res=>{
    //   console.log(res);
    //   this.tableData = res.data.records;
    //   this.total = res.data.total;
    // })

    //新写法
    this.request.get("/role/page", {
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
      this.tableData = res.data.records;
      this.total = res.data.total;
    })

  }
,
  //每页展示的条数
  handleSizeChange(val)
  {
    this.pageSize = val;
    this.selectAll();
  }
,
  //当前选择的是第几页
  handleCurrentChange(val)
  {
    this.pageNum = val;
    this.selectAll();
  }
,
  //模糊查询的搜索按钮
  select()
  {
    this.request.get("/role/page", {
      params: {
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        name: this.name,
      }
    }).then(res => {
      this.tableData = res.data.records;
      this.total = res.data.total;
    })
  }
,
  //重置按钮
  chongzhi()
  {
    this.name = '';
    this.selectAll();
  }
,
  //表单提交按钮
  onSubmit()
  {
    this.request.post("/role", this.form).then(res => {
      if (res.code === '200') {
        this.$message.success("提交成功");
        this.dialogVisible = false; //隐藏对话框
        this.selectAll();
        this.form = {};
      } else {
        this.$message.error("提交失败，请重新提交");
      }
    })
  }
,
  //编辑按钮
  handleEdit(row)
  {
    this.form = row;
    this.dialogVisible = true;
  }
,
  //删除按钮
  del(id)
  {
    this.request.delete("/role/" + id).then(res => {
      if (res.code === '200') {
        this.$message.success("删除成功");
        this.selectAll();
      } else {
        this.$message.error("删除失败");
      }
    })
  }
,
  //表格前面的checkbox按钮事件
  handleSelectionChange(val)
  {
    this.multipleSelection = val;
  }
,
  //批量删除按钮
  delBatch()
  {
    let ids = this.multipleSelection.map(v => v.id) //[{},{},{},...] => [1,2,3]
    this.request.post("/role/del/batch", ids).then(res => {
      if (res.code === '200') {
        this.$message.success("批量删除成功");
        this.selectAll();
      } else {
        this.$message.error("批量删除失败");
      }
    })
  }
,
  //分配菜单按钮
  selectMenu(role)
  {
    //提前保存好这两个数据，其他方法用，这里不用
    this.roleId=role.id;
    this.roleFlag=role.flag;
    //请求菜单数据
    this.request.get("/menu",{
      params:{
        name:""
      }
    }).then(res=>{
      this.MenuData=res.data;
      //把后台返回的菜单数据处理成id数组
      this.expands=res.data.map(v => v.id);
    })

    this.request.get("/role-menu/roleMenu/"+this.roleId).then(res=>{
      //先渲染弹窗里的元素
      this.menuDialogVis=true;

      //获取到当前用户的菜单id数组
      this.checks=res.data;

      this.request.get("/menu/ids").then(r=>{
        //获取到系统所有的菜单id
        const ids=r.data;
        ids.forEach(id=>{
          //如果当前用户的某一个菜单id不包含在系统所有的菜单里面
          if(!this.checks.includes(id)){
              //就把这个菜单设置为未选中的状态
            this.$refs.tree.setChecked(id,false);
          }
        })
      })
    })
  },
  //树形控件表单提交按钮
  saveRoleMenu(){
    this.request.post("/role-menu/roleMenu/"+this.roleId,this.$refs.tree.getCheckedKeys()).then(res=>{
      if(res.code==='200'){
        this.$message.success("绑定成功");
        //隐藏对话框
        this.menuDialogVis=false;

        //操作管理员角色后需要重新登录
        if(this.roleFlag==='ROLE_ADMIN'){
          this.$store.commit("logout");
        }
      }else{
        this.$message.success(res.msg);
      }
    })
  }
}
}
</script>

<style>
.headerBg {
  background: #ccc !important;
}
</style>