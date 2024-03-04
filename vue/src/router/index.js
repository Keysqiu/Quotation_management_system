import Vue from 'vue'
import VueRouter from 'vue-router'
import store from "@/store";

Vue.use(VueRouter)

const routes = [
    {
        path: '/register',
        name: '注册',
        component: () => import('../views/Register.vue')
    },
    {
        path: '/login',
        name: '登录',
        component: () => import('../views/Login.vue')
    },
    {
        path: '/404',
        name: '404',
        component: () => import('../views/404.vue')
    },
    {
        path: '/alipay',
        name: '阿里云沙箱支付',
        component: () => import('../views/alipay.vue')
    }
]

const router = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes
})


//提供一个重置路由的方法
export const resetRouter=()=>{
    router.matcher=new VueRouter({
        mode: 'history',
        base: process.env.BASE_URL,
        routes
    })
}

//注意：刷新页面会导致页面路由重置
export const setRoutes = () => {
    const storeMenus = localStorage.getItem("menus");
    if (storeMenus) {
        //读取当前的路由对象名称数组
        const currentRouteNames=router.getRoutes().map(v=>v.name);
        if(!currentRouteNames.includes('Manage')){ //不存在重复路由才添加进去
            //拼接动态路由
            const manageRoute = {path: '/',name:'Manage', component: () => import('../views/Manage.vue'), redirect: "/home", children: [
                    {path: '/person', name: '个人信息', component: () => import('../views/Person.vue')}
                ]}
            const menus = JSON.parse(storeMenus);
            menus.forEach(item => {
                if (item.path) { //当且仅当path不为空的时候才去设置路由

                    //因为上面中已有'/',所有要把获取到的子菜单的斜杠去掉（replace的用处）
                    let itemMenu = {path: item.path.replace("/", ""), name: item.name, component: () => import('../views/' + item.pagePath + '.vue')};
                    manageRoute.children.push(itemMenu);
                } else if (item.children.length) {
                    item.children.forEach(item => {
                        if (item.path) {
                            //因为上面中已有'/',所有要把获取到的子菜单的斜杠去掉（replace的用处）
                            let itemMenu = {
                                path: item.path.replace("/", ""),
                                name: item.name,
                                component: () => import('../views/' + item.pagePath + '.vue')
                            };
                            manageRoute.children.push(itemMenu);
                        }
                    })
                }

            })
            //动态添加到现在的路由对象中去
            router.addRoute(manageRoute);
            console.log(manageRoute)
        }
    }
}

//重置我就再set一次路由
setRoutes();

// 路由守卫（只要有路由跳转时，都会执行这里的代码）
router.beforeEach((to, from, next) => {
    localStorage.setItem("currentPathName", to.name)  // 设置当前的路由名称，为了在Header组件中去使用
    store.commit("setPath")  // 触发store的数据更新

    //未找到路由的情况
    if(!to.matched.length){
        const storeMenus=localStorage.getItem("menus");
        if(storeMenus){
            next("404")  // 放行路由
        }else{
            next("/login")
        }
    }

    //其他情况都放行
    next()

})

export default router