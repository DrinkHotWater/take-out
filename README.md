# take-out
a learning take-out program

springboot + mybatisPlus

外卖系统：分为管理员端和用户端
完成了员工管理、菜品管理、分类管理、套餐管理、订单管理、地址管理、购物车管理、客户管理共9类API。

将前端资源部署在Nginx服务器上，将请求反向代理转发给后端服务器。

使用1主2从的mysql集群，主从复制，主写从读。

使用redis缓存用户session来实现单点登录，缓存菜品数据、套餐数据来提高服务效率。


