--
    1.生成的代码直接复制到工程中作为一个新的子工程即可
    2.主工程pom中记得加入此项目的依赖
    3.nacos-application.yml里的内容为nacos存储的配置信息，无特殊变动粘贴到nacos中即可，dataId格式为moduleName + '.yml'
      group无特殊情况请不要更改。
    4.com.xqoo包下请自行再建立一个包名用来标识项目包，切勿直接与Application**.java文件同级编写代码，容易造成未知的一些依赖扫描等问题
