<template>
  <div>
    手机号码<input placeholder="输入手机号码" v-model="phone"><br>
    密码<input placeholder="密码" v-model="password"><br>
    <a href="javascript:void(0)" @click="userRegister">注册</a>
  </div>
  <br>
  <div>
    手机号码<input placeholder="输入手机号码" v-model="phone"><br>
    邮箱<input placeholder="输入邮箱" v-model="email"><br>
    密码<input placeholder="密码" v-model="password"><br>
    <a href="javascript:void(0)" @click="userLogin">登录</a>
  </div>
  <br>
  <div>
    <button @click="getUserInfo">查询用户信息</button><br>
    用户id:<span>{{user.id}}</span><br>
    手机号码:<span>{{user.phone}}</span><br>
    电子邮箱:<span>{{user.email}}</span><br>
    创建日期:<span>{{user.createTime}}</span><br>
    昵称:<span>{{user.nick}}</span><br>
    签名:<span>{{user.sign}}</span><br>
    性别:<span>{{user.gender}}</span><br>
    生日:<span>{{user.birth}}</span><br>
  </div>

  <div>
    <br><br>修改手机号、邮箱和密码(不修改则不填)<br>
    <input placeholder="手机号码" v-model="submitUser.phone"><br>
    <input placeholder="邮箱" v-model="submitUser.email"><br>
    <input placeholder="密码" v-model="submitUser.password"><br>
    <button @click="updateUser">提交</button>
  </div>

</template>

<script>
import Axios from 'axios'
import {JSEncrypt} from "jsencrypt";

const jse = new JSEncrypt();
Axios.defaults.baseURL = '/api'
export default {  //vue component
  name: 'login',
  data() {
    return {
      phone: '11',
      email: '',
      password: '11',
      userToken:'',
      user:{
        id:'',
        phone:'',
        email:'',
        createTime:'',
        nick:'',
        sign:'',
        gender:'',
        birth:''
      },
      submitUser:{
        phone:'',
        email:'',
        password:''
      }
    }
  },
  methods: {
    async userRegister() {
      let that = this
      //同步请求，保证先获取到公钥
      await Axios.get('/rsa-pks').then(res=>{
        jse.setPublicKey(res.data.data)
      },err=>{
        console.log(err.response.data.message)
      })
      Axios.post('/users', {
        phone: that.phone,
        password: jse.encrypt(that.password) //将加密后的密码发送给服务器
      }).then(function (response) {
        console.log(response)
        alert(response.data.msg)
      },function(err){
        alert('注册失败：'+err.response.data.message)
      })
    },
    async userLogin() {
      //同步请求，保证先获取到公钥
      await Axios.get('/rsa-pks').then(res=>{
        jse.setPublicKey(res.data.data)
      },err=>{
        console.log(err.response.data.message)
      })
      let that = this
      Axios.post('/user-tokens', {
        phone: that.phone,
        email: that.email,
        password:jse.encrypt(that.password)
      }).then(function (response){
        console.log(response)
        that.userToken=response.data.data
        alert('成功获取用户token，登录成功')
      },function (error){
        alert('登录失败：'+error.response.data.message)
      })
    },
    getUserInfo(){
      let that=this;
      Axios.get('/users',{
        headers:{
          token:that.userToken
        }
      }).then(res=>{
        console.log(res.data)
        let info=res.data.data
        that.user.id=info.id
        that.user.phone=info.phone
        that.user.emailr=info.email
        that.user.createTime=info.createTime
        that.user.nick=info.userinfo.nick
        that.user.sign=info.userinfo.sign
        that.user.gender=info.userinfo.gender
        that.user.birth=info.userinfo.birth
      },err=>{
        alert('未登录！')
        console.log(err.response.data.message)
      })
    },
    updateUser(){
      let that=this
      Axios.put('/users',   {
        phone:that.submitUser.phone,
        email:that.submitUser.email,
        password:(that.submitUser.password==='')?'':jse.encrypt(that.submitUser.password),
      },{
        headers:{
          token:that.userToken
        }
      }).then(res=>{
          alert(res.data.msg)
      },err=>{
        alert(err.response.data.message)
      })
    }
  }
}
</script>