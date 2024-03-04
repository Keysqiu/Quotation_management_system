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
      <el-button type="success" class="ml-5" round icon="el-icon-document" @click="dialogVisible = true">一键生成报价单</el-button>
    </div>
    <!--主体的表格-->
    <el-table :data="tableData" border stripe header-cell-class-name="headerBg"  @selection-change="handleSelectionChange">
      <el-table-column prop="pageGoods.goodName" label="商品名称" width="180" align="center">
      </el-table-column>
      <el-table-column prop="pageGoods.goodPrice" label="商品价格" width="200" align="center">
      </el-table-column>
      <el-table-column prop="pageGoods.goodKind" label="商品类型" width="300" align="center">
      </el-table-column>
      <el-table-column prop="username" label="供应商名称" width="300" align="center">
      </el-table-column>
      <el-table-column prop="phone" label="供应商联系电话" width="250" align="center">
      </el-table-column>
      <el-table-column prop="address" label="供应商地址" width="240" align="center">
      </el-table-column>
      <el-table-column prop="pageGoods.create_time" label="发布时间" width="200" align="center">
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
        title="生成报价单"
        :visible.sync="dialogVisible"
        width="30%"
    >
      <el-form ref="form" :model="selectGood" label-width="80px">
        <el-form-item label="商品名称(必填)">
          <el-input v-model="selectGood.goodName"></el-input>
        </el-form-item>

        <el-form-item label="商品价格">
          <el-input v-model="selectGood.goodPrice"></el-input>
        </el-form-item>

        <el-form-item label="商品类型">
          <el-input v-model="selectGood.goodKind"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onGoods_Submit">提交</el-button>
          <el-button @click="cannel">取消</el-button>
        </el-form-item>
      </el-form>

    </el-dialog>
  </div>
</template>

<script>
import * as echarts from 'echarts';
import Header from "@/components/Header";
import {Message} from "element-ui";


export default {
  name: "Home",
  data() {
    return {
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
      selectGood:{
        goodName:'',
        goodPrice:'',
        goodKind:''
      },
      //是否显示一键生成报价单窗口
      dialogVisible: false,
      username : localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")).username : "",
      role : localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")).role : ""
    }
  },
  components:{
    Header
  },
  created() {
    this.selectAll();
  },
  mounted() {
    //页面渲染完成时触发
    this.role = localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")).role : ""
    console.log("this.role", this.role)
    if (this.role === 'ROLE_ADMIN') {
      //折线图和柱状图
      var option = {
        //标题
        title: {
          text: '各季度会员数量统计',
          subtext: '趋势图',
          left: 'center'
        },
        tooltip: {
          trigger: 'item'
        },
        //X轴
        xAxis: {
          type: 'category',
          data: ["第一季度", "第二季度", "第三季度", "第四季度"]
        },
        //Y轴
        yAxis: {
          type: 'value'
        },
        //Y轴数据
        series: [
          {
            data: [],
            type: 'line'
          },
          {
            data: [],
            type: 'bar'
          }
        ]
      };

      //写死的
      var chartDom = document.getElementById('main');
      var myChart = echarts.init(chartDom);

      //饼图
      var pieOption = {
        //标题
        title: {
          text: '各季度会员数量统计',
          subtext: '比例图',
          left: 'center'
        },
        tooltip: {
          trigger: 'item'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            type: 'pie',
            //设置饼的大小
            radius: '60%',
            //数据
            data: [],
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            },
            //标签
            label: {
              normal: {
                show: true,
                position: 'inside',
                formatter: '{d}%',//模板变量有 {a}、{b}、{c}、{d}，分别表示系列名，数据名，数据值，百分比。{d}数据会根据value值计算百分比
                textStyle: {
                  align: 'center',
                  baseline: 'middle',
                  fontFamily: '微软雅黑',
                  fontSize: 15,
                  fontWeight: 'bolder'
                }
              },
            }
          }
        ]
      };

      //写死的
      var pieDom = document.getElementById('pie');
      var pieChart = echarts.init(pieDom);

      this.request.get("/echarts/members").then(res => {
        //填空
        option.series[0].data = res.data;
        option.series[1].data = res.data;
        option && myChart.setOption(option);

        pieOption.series[0].data = [
          {name: "第一季度", value: res.data[0]},
          {name: "第二季度", value: res.data[1]},
          {name: "第三季度", value: res.data[2]},
          {name: "第四季度", value: res.data[3]}
        ]
        pieOption && pieChart.setOption(pieOption);
      })
    }

  },
  methods:{
    selectAll() {
      //请求分页查询数据
      //旧写法
      // axios.get("http://localhost:9090/user/page?pageNum="+this.pageNum+"&pageSize="+this.pageSize).then(res=>{
      //   console.log(res);
      //   this.tableData = res.data.records;
      //   this.total = res.data.total;
      // })

      //请求数据库中的全部商品信息
      this.request.get("/goods/page/selectAll", {
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
        this.tableData = res.data;
        this.total = res.data.length;
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
      this.selectGood={};
      this.dialogVisible = false;
    },
    onGoods_Submit() {
      if(this.selectGood.goodName===''||this.selectGood.goodName===undefined){
        this.$message.error("请输入商品名称");
        return;
      }
      window.open("http://localhost:9090/goods/selectGood?goodName="+this.selectGood.goodName+"&goodPrice="+this.selectGood.goodPrice+"&goodKind="+this.selectGood.goodKind);
      this.selectGood={};
      this.dialogVisible = false;
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
      this.request.get("/goods/page/obscure", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          goodName:this.good_name,
          goodPrice:this.good_price,
          goodKind:this.good_kind,
        }
      }).then(res => {
        this.tableData = res.data;
        this.total = res.data.length;
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