const vm=new Vue({
    el:'#root',
    data(){
       return {jokes:[]}
    },
    methods:{

    },
    created() {
        this_=this
        axios.get("https://autumnfish.cn/api/joke/list?num=3").then(function(response){
            console.log(response.data)
            // data=JSON.parse(response.data)
            this_.jokes=response.data.jokes
        },function(err){
            console.log(err)
        })
    },
})