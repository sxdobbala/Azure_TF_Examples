import { MenuItemType, MenuItem } from '@bdroid/navigator';

export const defaultMenu: MenuItem[] = [
  {
    name: 'Home',
    icon: 'home',
    link: '/home',
  },
  {
    name: 'Dashboard',
    icon: 'dashboard',
    link: '/dashboard',
  },
  // {
  //   name: 'Custom components',
  //   type: SidenavItemType.Separator,
  // },
  {
    name: 'Grid',
    icon: 'insert_chart',
    disabled: true,
    children: [
      {
        name: 'CRUD Table',
        icon: 'web_aaset',
        link: '/dashboard/grid/crud-table',
      },
      {
        name: 'Grid List',
        icon: 'grid_on',
        link: '/dashboard/grid/grid-list',
      },
    ],
  },
  {
    name: 'Tenant',
    icon: 'group',
    disabled: false,
    children: [
      {
        name: 'Master',
        icon: 'apps',
        link: '/dashboard/tenants/tenant-master-view',
      },
      {
        name: 'Edges',
        icon: 'domain',
        link: '/dashboard/tenants/tenant-edge-view',
      },
      {
        name: 'Hive Servers',
        icon: 'developer_board',
        link: '/dashboard/tenants/tenant-hive-server-view',
      },
      {
        name: 'Packages',
        icon: 'work_outline',
        link: '/dashboard/tenants/tenant-packages',
      },
      {
        name: 'Data Sources',
        icon: 'settings_application',
        link: '/dashboard/tenants/tenant-data-source',
      },
      {
        name: 'Compute Allocation',
        icon: 'list_alt',
        link: '/dashboard/tenants/tenant-compute',
      }
    ],
  },
  {
    name: 'Chargeback',
    icon: 'wb_incandescent',
    disabled: false,
    children: [
      {
        name: 'Chargeback',
        icon: 'wb_incandescent',
        link: '/dashboard/tenants/tenant-chargeback',
      },
      {
        name: 'Chargeback Profile',
        icon: 'settings_application',
        link: '/dashboard/tenants/tenant-chargeback-profile',
      },
      {
        name: 'Chargeback Summary',
        icon: 'data_usage',
        link: '/dashboard/tenants/tenant-chargeback-summary',
      }
    ],
  },
  {
    name: 'Experiments',
    icon: 'pie_chart_outlined',
    disabled: true,
    children: [
      {
        name: 'Animations',
        icon: 'view_list',
        link: '/dashboard/experiments/animations',
      },
      {
        name: 'Layout',
        icon: 'apps',
        link: '/dashboard/experiments/layout',
      },
    ],
  },
  {
    name: 'Multi-Level Menu',
    icon: 'menu',
    disabled: true,
    children: [
      {
        name: 'Level 1',
        link: '/level1',
        children: [
          {
            name: 'Level 2',
            link: '/level1/level2',
            children: [
              {
                name: 'Level 3',
                link: '/level1/level2/level3',
                children: [
                  {
                    name: 'Level 4',
                    link: '/level1/level2/level3/level4',
                    children: [
                      {
                        name: 'Level 5',
                        link: '/level1/level2/level3/level4/level5',
                      },
                    ],
                  },
                ],
              },
            ],
          },
        ],
      },
    ],
  },
];

export const adminMenu: MenuItem[] = [
  {
    name: 'Dashboard',
    type: MenuItemType.DropDown,
    icon: 'dashboard',
    badge: { value: 1, color: 'accent' },
    tooltip: 'Dashboard',
    children: [
      {
        name: 'Dashboard',
        link: '/dashboard',
        icon: 'dashboard',
      },
      {
        name: 'Products',
        link: '/dashboard/products',
        icon: 'dashboard',
      },
      {
        name: 'Orders',
        link: '/dashboard/orders',
        icon: 'dashboard',
      },
    ],
  },
  {
    name: 'Custom components',
    type: MenuItemType.Separator,
  },
];

export const demoMenu: MenuItem[] = [
  {
    name: 'Dashboard',
    type: MenuItemType.DropDown,
    icon: 'dashboard',
    badge: { value: 1, color: 'accent' },
    tooltip: 'Dashboard',
    children: [
      {
        name: 'Dashboard',
        link: '/dashboard',
        icon: 'dashboard',
      },
      {
        name: 'Products',
        link: '/dashboard/products',
        icon: 'dashboard',
      },
      {
        name: 'Orders',
        link: '/dashboard/orders',
        icon: 'dashboard',
      },
    ],
  },
  {
    name: 'Custom components',
    type: MenuItemType.Separator,
  },
];
