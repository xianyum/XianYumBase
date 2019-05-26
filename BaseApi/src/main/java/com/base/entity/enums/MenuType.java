package com.base.entity.enums;

/**
 * @author zhangwei
 * @date 2019/5/26 16:55
 * @email 80616059@qq.com
 */
public enum MenuType {
        /**
         * 目录
         */
        CATALOG(0),
        /**
         * 菜单
         */
        MENU(1),
        /**
         * 按钮
         */
        BUTTON(2);

        private int value;

        MenuType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
}
