<template>
  <div>
  <el-container style="min-height: 100vh">
    <!-- 菜单栏 -->
    <el-aside :width="sideWidth+'px'"
              style="box-shadow: 2px 0 6px rgba(0,21,41,0.35)">
      <Aside :isCollapse="isCollapse" :logoTextShow="logoTextShow" :menus="user"></Aside>
    </el-aside>

    <el-container>
      <!-- 行头 -->
      <el-header style="border-bottom: 1px solid #ccc;">
      <Header :collapseBtnClass="collapseBtnClass"  @asideCollapse="collapse" :user="user"  ></Header>
      </el-header>
      <el-main>
<!--        表示当前页面的子路由会在<router-view/>里面展示-->
        <router-view @refreshUser="getUser"/>

      </el-main>
    </el-container>
  </el-container>
  </div>
</template>


<script>
import Aside from "@/components/Aside";
import axios from "axios";
import Header from "@/components/Header";

export default {
  data() {
    return {

      //点击菜单时动态改变样式
      collapseBtnClass: 'el-icon-s-fold',
      isCollapse: false,
      sideWidth: 200,
      //是否展示logo旁边的文字
      logoTextShow: true,
      user:{},
      role : localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")).role : ""
    }
  },
  components:{
    Header,
    Aside
  },
  created() {
    // 从后台获取最新的User数据
    this.getUser()
  },
  methods: {
    //点击收缩按钮触发
    collapse() {
      console.log("11111")
      this.isCollapse = !this.isCollapse;
      //收缩
      if (this.isCollapse) {
        this.sideWidth = 64;
        this.collapseBtnClass = 'el-icon-s-unfold';
        this.logoTextShow = false;
      } else {
        //  展开
        this.sideWidth = 200;
        this.collapseBtnClass = 'el-icon-s-fold';
        this.logoTextShow = true;
      }
    },
    getUser() {
      let username = localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")).username : ""
      if(this.role==="ROLE_PROVIDER"){
        // 从后台获取User数据
        this.request.get("/provider/username/" + username).then(res => {
          console.log("res",res.code);
          if(res.code=='401') {
            this.$store.commit("logout");
            return;
          }
          console.log("userinfo",res.data);
          // 重新赋值后台的最新User数据
          this.user = res.data
        })
      }else{
        // 从后台获取User数据
        this.request.get("/user/username/" + username).then(res => {
          console.log("res",res.code);
          if(res.code=='401') {
            this.$store.commit("logout");
            return;
          }
          console.log("userinfo",res.data);
          // 重新赋值后台的最新User数据
          this.user = res.data
        })
      }
    }
  }
};
</script>

<style>
.el-header {
  background-color: #B3C0D1;
  color: #333;
  line-height: 60px;
}

.el-aside {
  color: #333;
}


</style>
