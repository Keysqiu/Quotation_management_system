<template>
  <div>
    <!--搜索区域-->
    <div style="margin: 10px 0">
      <el-input style="width: 200px" prefix-icon="el-icon-search" placeholder="请输入名称" v-model="username"></el-input>
      <el-input v-if="value==='0'" style="width: 200px;padding-left: 5px" prefix-icon="el-icon-message"
                placeholder="请输入邮箱"
                v-model="email"></el-input>
      <el-input v-else style="width: 200px;padding-left: 5px" prefix-icon="el-icon-phone-outline" placeholder="请输入电话"
                v-model="phone"></el-input>
      <el-input style="width: 200px;padding-left: 5px" prefix-icon="el-icon-position"
                placeholder="请输入地址" v-model="address"></el-input>
      <el-button type="primary" class="ml-5" @click="select">搜索</el-button>
      <el-button type="warning" class="ml-5" @click="chongzhi">重置</el-button>
    </div>
    <!--操作按钮 -->
    <div style="margin: 10px 0">
      <el-select v-model="value" placeholder="" style="width: 100px;padding-right: 5px" @change="handleChange">
        <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
        >
        </el-option>
      </el-select>
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
      <el-upload :action="value==='0'?'http://localhost:9090/user/import':'http://localhost:9090/provider/import'"
                 :show-file-list="false" accept="xlsx"
                 :on-success="handleExcelSuccess" style="display: inline-block">
        <el-button type="primary" class="ml-5"><i class="el-icon-download" style="padding-right: 5px"></i>导入</el-button>
      </el-upload>
      <el-button type="primary" class="ml-5" @click="exp"><i class="el-icon-upload2" style="padding-right: 5px"></i>导出
      </el-button>
    </div>
    <!--主体的表格-->
    <el-table :data="tableData" border stripe header-cell-class-name="headerBg"
              @selection-change="handleSelectionChange">
      <el-table-column
          type="selection"
          width="55">
      </el-table-column>
      <el-table-column prop="id" label="ID" width="80" align="center">
      </el-table-column>
      <el-table-column prop="username" label="用户名" width="140" align="center">
      </el-table-column>
      <el-table-column prop="role" label="角色" width="120" align="center">
      </el-table-column>
      <el-table-column prop="nickname" label="昵称" width="120" align="center">
      </el-table-column>
      <el-table-column prop="email" label="邮箱" width="200" align="center">
      </el-table-column>
      <el-table-column prop="phone" label="电话" width="200" align="center">
      </el-table-column>
      <el-table-column prop="address" label="地址" align="center">
      </el-table-column>
      <el-table-column prop="create_time" label="注册时间" width="200" align="center">
      </el-table-column>
      <el-table-column label="操作" width="200" align="center">
        <template slot-scope="scope">
          <!--              编辑按钮-->
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

    <!--添加用户数据对话框表单-->
    <el-dialog
        title="编辑用户信息"
        :visible.sync="dialogVisible"
        width="30%"
    >
      <el-form ref="form" :model="addUser" label-width="80px">
        <el-form-item label="用户名">
          <el-input disabled v-model="addUser.username"></el-input>
        </el-form-item>
        <el-form-item label="角色">
          <el-select clearable v-model="addUser.role" placeholder="请选择角色" style="width: 100%">
            <el-option v-for="item in roles" :keys="item.name" :label="item.name" :value="item.flag"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="密码">
          <el-input v-model="addUser.password"></el-input>
        </el-form-item>

        <el-form-item label="昵称">
          <el-input v-model="addUser.nickname"></el-input>
        </el-form-item>

        <el-form-item label="邮箱" v-if="addUser.role!='ROLE_PROVIDER'">
          <el-input v-model="addUser.email"></el-input>
        </el-form-item>

        <el-form-item label="电话">
          <el-input v-model="addUser.phone"></el-input>
        </el-form-item>

        <el-form-item label="地址">
          <el-input v-model="addUser.address"></el-input>
        </el-form-item>

        <el-form-item label="地址">
          <el-input v-model="addUser.address"></el-input>
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
import {serverIp} from "../../public/config";
import {Message} from "element-ui";

export default {
  name: "User",
  data() {
    return {
//模糊查询时的字段
      username: '',
      email: '',
      address: '',
      phone: '',
      //查询的页数和每页查询的个数
      pageNum: 1,
      pageSize: 10,
      //用户表
      tableData: [],
      //用户总人数
      total: 0,
      //是否显示新增表单
      dialogVisible: false,
      addUser: {
        id: '',
        username: '',
        password: '',
        nickname: '',
        email: '',
        phone: '',
        address: '',
        role: ''
      },
      //用来装批量删除选择的项
      multipleSelection: [],
      headBg: 'headBg',
      //角色数组
      roles: [],
      //查看用户信息的类型
      options: [{
        value: '0',
        label: '用户'
      }, {
        value: '1',
        label: '供应商'
      }],
      value: '0',
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
      //请求用户数据
      this.request.get("/user/page/selectAll", {
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
      this.request.get("/role").then(res => {
        this.roles = res.data;
        console.log("role", res.data);
      })
    },
    selectAll_Provider() {
      this.request.get("/provider/page/selectAll", {
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
      this.request.get("/role").then(res => {
        this.roles = res.data;
        console.log("role", res.data);
      })
    },
    //每页展示的条数
    handleSizeChange(val) {
      this.pageSize = val;
      if (this.value === '0') {
        this.selectAll();
      } else {
        this.selectAll_Provider();
      }
    },
    //当前选择的是第几页
    handleCurrentChange(val) {
      this.pageNum = val;
      if (this.value === '0') {
        this.selectAll();
      } else {
        this.selectAll_Provider();
      }
    },
    cannel() {
      this.addUser = {};
      this.dialogVisible = false;
      if (this.value === '0') {
        this.selectAll();
      } else {
        this.selectAll_Provider();
      }
    },
    //模糊查询的搜索按钮
    select() {
      if (this.value === '0') {
        //对普通用户信息进行模糊查询
        this.request.get("/user/page", {
          params: {
            pageNum: this.pageNum,
            pageSize: this.pageSize,
            username: this.username,
            email: this.email,
            address: this.address
          }
        }).then(res => {
          this.tableData = res.data.records;
          this.total = res.data.total;
        })
      } else {
        //对供应商信息进行模糊查询
        this.request.get("/provider/page", {
          params: {
            pageNum: this.pageNum,
            pageSize: this.pageSize,
            username: this.username,
            phone: this.phone,
            address: this.address
          }
        }).then(res => {
          this.tableData = res.data.records;
          this.total = res.data.total;
        })
      }
    },
    //重置按钮
    chongzhi() {
      this.username = '';
      this.email = '';
      this.address = '';
      if (this.value === '0') {
        this.selectAll();
      } else {
        this.selectAll_Provider();
      }
    },
    //表单提交按钮
    onSubmit() {
      console.log('role', this.addUser.role);
      if (this.addUser.role === '' || this.addUser.role === undefined) {
        this.$message.warning("未选择角色，请重新填写");
        this.addUser = {};
        return;
      }
      if (this.value === '0') {
        //修改用户信息
        this.request.post("/user", this.addUser).then(res => {
          if (res.code === '200') {
            this.$message.success("提交成功");
            this.dialogVisible = false; //隐藏对话框
            this.selectAll();
            this.addUser = {};
          } else {
            this.$message.error("提交失败，请重新提交");
          }
        })
      } else {
        //修改供应商信息
        this.request.post("/provider", this.addUser).then(res => {
          if (res.code === '200') {
            this.$message.success("提交成功");
            this.dialogVisible = false; //隐藏对话框
            this.selectAll_Provider();
            this.addUser = {};
          } else {
            this.$message.error("提交失败，请重新提交");
          }
        })
      }

    },
    //编辑按钮
    handleEdit(row) {
      this.addUser = row;
      this.dialogVisible = true;
    },
    //删除按钮
    del(id) {
      if (this.value === '0') {
        //删除普通用户
        this.request.delete("/user/" + id).then(res => {
          if (res.code === '200') {
            this.$message.success("删除成功");
            this.selectAll();
          } else {
            this.$message.error("删除失败");
          }
        })
      } else {
        //删除供应商
        this.request.delete("/provider/" + id).then(res => {
          if (res.code === '200') {
            this.$message.success("删除成功");
            this.selectAll_Provider();
          } else {
            this.$message.error("删除失败");
          }
        })
      }
    },
    //表格前面的checkbox按钮事件
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    //批量删除按钮
    delBatch() {
      if (this.value === '0') {
        //批量删除普通用户
        let ids = this.multipleSelection.map(v => v.id) //[{},{},{},...] => [1,2,3]
        this.request.post("/user/del/batch", ids).then(res => {
          if (res.code === '200') {
            this.$message.success("批量删除成功");
            this.selectAll();
          } else {
            this.$message.error("批量删除失败");
          }
        })
      } else {
        //批量删除供应商
        let ids = this.multipleSelection.map(v => v.id) //[{},{},{},...] => [1,2,3]
        this.request.post("/provider/del/batch", ids).then(res => {
          if (res.code === '200') {
            this.$message.success("批量删除成功");
            this.selectAll_Provider();
          } else {
            this.$message.error("批量删除失败");
          }
        })
      }
    },
    //导出按钮
    exp() {
      if (this.value === '0') {
        //导出普通用户信息
        window.open("http://localhost:9090/user/export")
      } else {
        window.open("http://localhost:9090/provider/export")
      }
    },
    //导入信息
    handleExcelSuccess() {
      this.$message.success("导入成功");
      if (this.value === '0') {
        this.selectAll();
      } else {
        this.selectAll_Provider();
      }
    },
    handleClick() {
      alert('button click');
    },
    handleChange(val) {
      if (val === '1') {
        //选择供应商
        this.request.get("/provider/page/selectAll", {
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
      } else {
        //选择用户
        this.request.get("/user/page/selectAll", {
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
    }
  }
}
</script>

<style>
.headerBg {
  background: #ccc !important;
}

.el-dropdown {
  vertical-align: top;
}

.el-dropdown + .el-dropdown {
  margin-left: 15px;
}

.el-icon-arrow-down {
  font-size: 12px;
}
</style>