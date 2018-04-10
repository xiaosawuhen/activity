##Activiti5.10整合Spring完成请假流程
####Spring+Activiti+Spring Data JPA
* 流程为:上传流程定义文件+填写请假条(启动工作流)+部门经理审批+人事审批+销假+邮件通知+结束
* 其中在部门经理和人事审批的时候可以驳回(重新申请节点),该节点用户可自由选择是否继续申请还是选择结束流程并选择是否需要邮件通知
* 该项目基于maven构建。使用mysql数据库。
* 创建数据库activiti
* 修改数据库连接信息
* 第一次启动服务器会自动创建Activiti所需的表,也会创建该项目中的请假实体表(leave)JPA引擎会自动创建
* 执行src/main/resources/sql/data.sql脚本,初始化activiti所需的数据(用户、组、及用户和组之间的关联信息)
![src/main/resources/01.jpg](src/main/resources/01.jpg)
![src/main/resources/02.jpg](src/main/resources/02.jpg)
![src/main/resources/03.jpg](src/main/resources/03.jpg)
![src/main/resources/04.jpg](src/main/resources/04.jpg)
![src/main/resources/05.jpg](src/main/resources/05.jpg)
![src/main/resources/06.jpg](src/main/resources/06.jpg)