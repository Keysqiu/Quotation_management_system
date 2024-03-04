<template>
  <div style="line-height: 60px; display:flex;">
    <!--点击收缩的那个图标-->
    <div style="flex: 1;font-size: 20px">
      <span :class="collapseBtnClass" style="cursor: pointer;font-size: 18px" @click="collapse"></span>
      <!--        面包屑-->
      <el-breadcrumb separator="/" style="display: inline-block; margin-left: 10px;">
        <el-breadcrumb-item :to="'/'">首页</el-breadcrumb-item>
        <el-breadcrumb-item>{{ currentPathName }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div>
      <el-dropdown>
        <div class="el-dropdown-link">
          <div v-if="user.avatarUrl">
<!--            <img :src="user.avatarUrl" alt=""-->
<!--                 style="width: 30px;border-radius: 50%;top: 10px;right: 5px">-->
            <el-avatar size="medium" :src="user.avatarUrl" class="avatarUrl-view"></el-avatar>
          </div>
          <div v-else>
            <img src="../assets/avator.png" alt=""
                 class="avatarUrl-view">
          </div>
          <!-- 昵称 -->
          <div>{{ user.nickname }}</div>
          <!--     下拉菜单<i class="el-icon-arrow-down el-icon&#45;&#45;right"></i>-->
        </div>
        <el-dropdown-menu slot="dropdown" style="width: 100px;text-align: center">
          <el-dropdown-item style="font-size: 14px;padding: 5px 0">
            <router-link to="/person">个人信息</router-link>
          </el-dropdown-item>
          <el-dropdown-item style="font-size: 14px;padding: 5px 0">
            <span style="text-decoration: none" @click="LogOut">退出</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
export default {
  name: "Header",
  //从父组件传进来的数据
  props: {
    collapseBtnClass: String,
    user: Object,
  },
  computed: {
    currentPathName() {
      return this.$store.state.currentPathName;　　//需要监听的数据
    }
  },
  watch: {
    currentPathName(newVal, oldVal) {
      console.log(newVal)
    }
  },
  data() {
    return {
      fit: 'fill',
      role : localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")).role : ""
    }
  },
  methods: {
    collapse() {
      // this.$parent.$parent.$parent.$parent.collapse()  // 通过4个 $parent 找到父组件，从而调用其折叠方法
      // 触发父级更新asideCollapse的方法
      this.$emit("asideCollapse")
    },
    //退出按钮
    LogOut() {
      this.$store.commit("logout");
      this.$message.success("退出成功");
    }
  }
}
</script>

<style scoped>
.el-dropdown-link {
  display: flex;
  cursor: pointer;
  color: #409EFF;
}
.avatarUrl-view{
  width: 30px;border-radius: 50%;margin-top: 13px;margin-right: 5px
}
.el-icon-arrow-down {
  font-size: 12px;
}

</style>