<template>
  <div class="wrapper">
    <div
        style="margin: 100px auto; background-color: #fff; width: 350px; height: 440px; padding: 20px; border-radius: 10px">
      <div style="margin: 20px 0; text-align: center; font-size: 24px"><b>注 册</b></div>
      <el-form :model="user" :rules="rules" ref="userForm">
        <el-select v-model="value" placeholder="请选择注册的类型" style="width: 100%;margin-bottom: 10px">
          <el-option
              v-for="item in options"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          >
          </el-option>
        </el-select>
        <!-- show-password是显示那个小眼睛 -->
        <el-form-item prop="username">
          <el-input placeholder="请输入用户名" size="medium" style="margin: 5px 0" prefix-icon="el-icon-user"
                    v-model="user.username"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input placeholder="请输入密码" size="medium" prefix-icon="el-icon-lock" show-password style="margin: 5px 0"
                    v-model="user.password"></el-input>
        </el-form-item>
        <el-form-item prop="ConfirmPassword">
          <el-input placeholder="请确认密码" size="medium" prefix-icon="el-icon-lock" show-password style="margin: 5px 0"
                    v-model="user.ConfirmPassword"></el-input>
        </el-form-item>
        <el-form-item>
          <div style="display: flex">
            <el-input size="mid" v-model="code" style="width: 200px"></el-input>
            <span @click="refreshCode" style="cursor: pointer; flex: 1;">
            <Identify :identifyCode="ideCode"></Identify>
            </span>
          </div>
        </el-form-item>
        <el-form-item style="margin: 10px 0; text-align: right">
          <el-button type="warning" size="small" autocomplete="off" @click="register">注册</el-button>
          <el-button type="primary" size="small" autocomplete="off" @click="$router.push('/login')">返回登录</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="footer flex-c">
      <a class="desc" href="https://beian.miit.gov.cn" target="_blank">
        桂ICP备2023000857号-1
      </a>
      <div>Copyright © 2023 - {{ new Date().getFullYear() }} 邱庆胜 All Rights Reserved</div>
    </div>
  </div>
</template>

<script>
import {setRoutes} from "@/router";
import axios from "axios";
import Identify from "@/components/Identify";

export default {
  name: "Login",
  data() {
    return {
      code: '',
      // 图片验证码
      ideCode: '',
      // 验证码规则
      ideCodes: '3456789ABCDEFGHGKMNPQRSTUVWXY',
      user: {},
      //表单校验规则，element框架form表单里有的（表单验证）
      rules: {
        username: [
          {required: true, message: '请输入用户名', trigger: 'blur'},
          {min: 3, max: 10, message: '长度在 3 到 5 个字符', trigger: 'blur'}
        ],
        password: [
          {required: true, message: '请输入密码', trigger: 'blur'},
          {min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur'}
        ],
        ConfirmPassword: [
          {required: true, message: '请输入密码', trigger: 'blur'},
          {min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur'}
        ]
      },
      //注册用户的类型
      options: [{
        value: '0',
        label: '用户'
      }, {
        value: '1',
        label: '供应商'
      }],
      value: '0'
    }
  },
  components:{
    Identify
  },
  created() {
    this.refreshCode();
    console.log('ideCode',this.ideCode);
  },
  methods: {
    // 切换验证码
    refreshCode() {
      this.ideCode = ''
      this.makeCode(this.ideCodes, 4);
    },
    // 生成随机验证码
    makeCode(o, l) {
      for (let i = 0; i < l; i++) {
        this.ideCode += this.ideCodes[Math.floor(Math.random() * (this.ideCodes.length))];
      }
    },
    //注册按钮
    register() {
      //验证二维码是否正确
      //要么全小写，要么全大写
      if (this.code !== this.ideCode.toLowerCase() && this.code !== this.ideCode) {
        this.$message({
          type: "error",
          message: "验证码错误，请重新填写"
        })
        return;
      }
      //如果表单验证都不通过，就不允许给后台发请求了，这个也是element框架form表单里有的（表单验证），还是so easy！
      this.$refs['userForm'].validate((valid) => {
        if (valid) {  // 表单校验合法
          if (this.user.password !== this.user.ConfirmPassword) {
            console.log("1111")
            this.$message.error("两次输入的密码不一致");
            return false;
          }
          if (this.value === '0') {
            //新用户注册
            this.request.post("/user/register", this.user).then(res => {
              if (res.code === '200') {
                this.$message.success("注册成功");
              } else {
                this.$message.error(res.msg);
              }
            })
          } else {
            //新供应商注册
            //新用户注册
            this.request.post("/provider/register", this.user).then(res => {
              if (res.code === '200') {
                this.$message.success("注册成功");
              } else {
                this.$message.error(res.msg);
              }
            })
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.wrapper {
  /* vh是一个视图单位，这里的100vh表示高度刚好撑满屏幕的整个高度 */
  height: 100vh;
  /* to bottom right 的意思是渐变色的渲染是从左上角到右下角 */
  /*background-image: linear-gradient(to bottom right, #FC466B, #3F5EFB);*/

  background-image: linear-gradient(to bottom right, #41ABFD, #FFB6C1);
  /* 超出部分隐藏掉 */
  overflow: hidden;
}
.footer {
  width: 100%;
  padding: 20px;
  z-index: 1;
  bottom: 0;
  position: fixed;
  text-align: center;
}
</style>
