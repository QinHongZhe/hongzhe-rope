import Mock from "mockjs";

/**
 *
 * 动态模拟菜单
 *
 * label菜单的名字
 * path菜单的路径
 * icon菜单的图标（系统采用的阿里巴巴图表库）
 * compnent组件的地址
 * children子类菜单数组
 * group配置其他路由激活菜单高亮
 */

var top = [
  {
    label: "首页",
    href: "/wel/index",
    icon: "el-icon-menu",
    parentId: 0
  }
];
const first = [
  {
    id: 100,
    label: "流程管理",
    path: "/process",
    icon: "icon-canshu",
    children: [
      {
        id: 100,
        label: "创建流程",
        path: "create",
        component: "views/process/create/index",
        icon: "el-icon-edit-outline",
        children: []
      },
      {
        id: 90,
        label: "流程管理",
        path: "manage",
        component: "views/process/manage/index",
        icon: "el-icon-notebook-2",
        children: []
      },
      {
        id: 90,
        label: "流程存储",
        path: "storage",
        component: "views/process/storage/index",
        icon: "el-icon-download",
        children: []
      },
      {
        id: 90,
        label: "导入流程",
        path: "import",
        component: "views/process/storage/import",
        icon: "el-icon-upload2",
        children: []
      }
    ]
  },
  {
    id: 90,
    label: "插件管理",
    path: "/plugin",
    icon: "icon-quanxian",
    children: [
      {
        id: 100,
        label: "插件信息",
        path: "info",
        component: "views/plugin/index",
        icon: "el-icon-paperclip",
        children: []
      },
      {
        id: 90,
        label: "安装插件",
        path: "install",
        component: "views/plugin/install",
        icon: "el-icon-attract",
        children: []
      }
    ]
  }
];

export default ({ mock }) => {
  if (!mock) return;
  let menu = [first];
  Mock.mock("/user/menu", "get", res => {
    let body = JSON.parse(res.body);
    return {
      data: menu[body.type] || []
    };
  });
  Mock.mock("/user/topMenu", "get", () => {
    return {
      data: top
    };
  });
};
