<template>
  <div class="wrapper">
    <div
        style="margin: 200px auto; background-color: #fff; width: 350px; height: 350px; padding: 20px; border-radius: 10px">
      <div style="margin: 20px 0; text-align: center; font-size: 24px"><b>登 录</b></div>
      <el-select v-model="value" placeholder="请选择注册的类型" style="width: 100%;margin-bottom: 10px">
        <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
        >
        </el-option>
      </el-select>
      <el-form :model="user" :rules="rules" ref="userForm">
        <el-form-item prop="username">
          <el-input size="medium" prefix-icon="el-icon-user" v-model="user.username"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input size="medium" prefix-icon="el-icon-lock" show-password
                    v-model="user.password"></el-input>
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
          <el-button type="warning" size="small" autocomplete="off" @click="$router.push('/register')">注册</el-button>
          <el-button type="primary" size="small" autocomplete="off" @click="login">登录</el-button>
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
      },
      //登录用户的类型
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
  },
  methods: {
    // 切换验证码
    refreshCode() {
      this.ideCode = ''
      this.makeCode(this.ideCodes, 4)
      console.log('code',this.ideCode);
    },
    // 生成随机验证码
    makeCode(o, l) {
      for (let i = 0; i < l; i++) {
        this.ideCode += this.ideCodes[Math.floor(Math.random() * (this.ideCodes.length))];
      }
    },
    //登录按钮
    login() {
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
          if (this.value === '0') {
            //用户登录
            this.request.post("/user/login", this.user).then(res => {
              if (res.code === '200') {
                console.log("222")
                localStorage.setItem("user", JSON.stringify(res.data)); // 存储用户信息到浏览器
                localStorage.setItem("menus", JSON.stringify(res.data.menus)); // 存储用户信息到浏览器

                //动态设置当前用户的路由
                setRoutes();
                this.$router.push("/");
                if (res.data.role === 'ROLE_USER') {
                  this.$message.success("普通用户登录成功");
                } else {
                  this.$message.success("管理员登录成功");
                }
              } else {
                console.log("333")
                this.$message.error(res.msg);
              }
            })
          } else {
            this.request.post("/provider/login", this.user).then(res => {
              if (res.code === '200') {
                console.log("222")
                localStorage.setItem("user", JSON.stringify(res.data)); // 存储用户信息到浏览器
                localStorage.setItem("menus", JSON.stringify(res.data.menus)); // 存储用户信息到浏览器

                //动态设置当前用户的路由
                setRoutes();
                this.$router.push("/");
                this.$message.success("供应商登录成功");
              } else {
                console.log("333")
                this.$message.error(res.msg);
              }
            })
          }
        }
      });
    }
  }
}
</script>

<style scoped>
.wrapper {
  /* vh是一个视图单位，这里的100vh表示高度刚好撑满屏幕的整个高度 */
  height: 100vh;
  /* to bottom right 的意思是渐变色的渲染是从左上角到右下角 */
  background-image: linear-gradient(to bottom right, #FC466B, #3F5EFB);
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
