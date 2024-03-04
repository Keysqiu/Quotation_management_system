<template>
  <el-card style="width: 500px">

    <el-upload
        class="avatar-uploader"
        :action="'http://' + serverIp + ':9090/file/upload'"
        :show-file-list="false"
        :on-success="handleAvatarSuccess"
    >
      <img v-if="form.avatarUrl" :src="form.avatarUrl" class="avatar">
      <i v-else class="el-icon-plus avatar-uploader-icon"></i>
    </el-upload>
    <el-form ref="form" :model="form" label-width="80px">
      <el-form-item label="用户名">
        <el-input disabled v-model="form.username"></el-input>
      </el-form-item>

      <el-form-item label="密码">
        <el-input disabled type="password" v-model="form.password"></el-input>
      </el-form-item>

      <el-form-item label="昵称">
        <el-input v-model="form.nickname"></el-input>
      </el-form-item>

      <el-form-item label="邮箱">
        <el-input v-model="form.email"></el-input>
      </el-form-item>

      <el-form-item label="电话">
        <el-input v-model="form.phone"></el-input>
      </el-form-item>

      <el-form-item label="地址">
        <el-input type="textarea" v-model="form.address"></el-input>
      </el-form-item>


      <el-form-item>
        <el-button type="primary" @click="onSubmit">修改</el-button>
      </el-form-item>

    </el-form>

  </el-card>
</template>

<script>
import {serverIp} from "../../public/config";


export default {
  name: "Person",
  data() {
    return {
      form: {},
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
      serverIp: serverIp,
      role: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")).role : ""
    }
  },
  //相当于wx的onLoad
  created() {
    this.getUser().then(res => {
      console.log(res)
      this.form = res
    })
  },
  methods: {
    //头像上传事件
    handleAvatarSuccess(res) {
      this.form.avatarUrl = res;
    },
    async getUser() {
      if (this.role === "ROLE_PROVIDER") {
        return (await this.request.get("/provider/username/" + this.user.username)).data
      } else {
        return (await this.request.get("/user/username/" + this.user.username)).data
      }
    },
    //修改按钮
    onSubmit() {
      if (this.role === "ROLE_PROVIDER") {
        this.request.post("/provider/save", this.form).then(res => {
          if (res.code === '200') {
            this.$message.success("修改成功");
            // 更新浏览器存储的用户信息
            this.request.get("/provider/username/" + this.form.username).then(res => {
              res.data.token = JSON.parse(localStorage.getItem("user")).token
              localStorage.setItem("user", JSON.stringify(res.data));
              // 触发父级更新User的方法
              this.$emit("refreshUser");
            })
          } else {
            this.$message.error("修改失败，请重新提交");
          }
        })
      } else {
        this.request.post("/user/save", this.form).then(res => {
          if (res.code === '200') {
            this.$message.success("修改成功");
            // 更新浏览器存储的用户信息
            this.request.get("/user/username/" + this.form.username).then(res => {
              res.data.token = JSON.parse(localStorage.getItem("user")).token
              localStorage.setItem("user", JSON.stringify(res.data));
              // 触发父级更新User的方法
              this.$emit("refreshUser");
            })
          } else {
            this.$message.error("修改失败，请重新提交");
          }
        })
      }
    },

  }
}
</script>

<style>
.avatar-uploader {
  text-align: center;
  padding-bottom: 10px;
}

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 138px;
  height: 138px;
  line-height: 138px;
  text-align: center;
}

.avatar {
  width: 138px;
  height: 138px;
  display: block;
}
</style>