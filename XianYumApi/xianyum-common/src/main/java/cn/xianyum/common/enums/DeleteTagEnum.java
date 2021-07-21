package cn.xianyum.common.enums;
/***
 * 删除状态枚举类
 * 0：没有被删除
 * 1：删除
 */
public enum DeleteTagEnum {
    Delete(0),//正常状态
    deleted(1);//已经被删除

    DeleteTagEnum(Integer deleteTag) {
        this.deleteTag = deleteTag;
    }

    private Integer deleteTag;

    public Integer getDeleteTag() {
        return deleteTag;
    }
}
