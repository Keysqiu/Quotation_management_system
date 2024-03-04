<template>
  <el-menu :default-openeds="opens" style="min-height: 100%;overflow-x: hidden"
           background-color="rgb(48,65,86)"
           text-color="#fff"
           active-text-color="#ffd04b"
           :collapse-transition="false"
           :collapse="isCollapse"
           router
  >
    <!--菜单栏顶部-->
    <div style="height: 60px;line-height: 60px;text-align: center">
      <img src="../assets/logo.png" style="width: 20px;position: relative;top:5px;margin-right: 5px">
      <b style="color: white" v-show="logoTextShow" v-if="role=='ROLE_ADMIN'">
        报价后台管理系统
      </b>
      <b style="color: white" v-show="logoTextShow" v-else>
        报价系统
      </b>
    </div>

    <div v-for="item in menus" :key="item.id">
      <!-- 有路径的为一级菜单 -->
      <div v-if="item.path">
        <el-menu-item :index="item.path">
            <i :class="item.icon"></i>
            <span slot="title">{{item.name}}</span>
        </el-menu-item>
      </div>
      <!-- 没有路径的为二级菜单 -->
      <div v-else>
        <el-submenu :index="item.id+''">
          <template slot="title">
            <i :class="item.icon"></i>
            <span slot="title">{{item.name}}</span>
          </template>
          <div v-for="subItem in item.children" :key="subItem.id">
            <el-menu-item :index="subItem.path">
              <template slot="title">
                <i :class="subItem.icon"></i>
                <span slot="title">{{subItem.name}}</span>
              </template>
            </el-menu-item>
          </div>
        </el-submenu>
      </div>
    </div>

  </el-menu>
</template>

<script>
export default {
  name: "Aside",
  //外面传数据进来写的位置
  props: {
    isCollapse: Boolean,
    logoTextShow:Boolean,
  },
  data(){
    return{
      role : localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")).role : "",
      menus:localStorage.getItem("menus")?JSON.parse(localStorage.getItem("menus")):[],
      opens:localStorage.getItem("menus")?JSON.parse(localStorage.getItem("menus")).map(v => v.id+''):[],
    }
  }
}
</script>

<style>
.el-menu-item.is-active {
  background-color: rgb(38, 52, 69) !important;
}
.el-menu-item:hover {
  background-color: rgb(38, 52, 69) !important;
}

.el-submenu__title:hover {
  background-color: rgb(38, 52, 69) !important;
}
/*解决收缩菜单文字不消失问题*/
.el-menu--collapse span {
  visibility: hidden;
}
</style>