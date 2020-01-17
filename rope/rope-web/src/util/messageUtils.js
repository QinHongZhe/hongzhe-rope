export const requestCall = (res, vue, msg)=> {
    const body = res.data;
    if(!body){
        vue.$message({
            message: `${msg}失败`,
            type: "error"
        });
    }
    if (body.code === 1) {
        vue.$message({
            message: `${msg}成功`,
            type: "success"
        });
    } else {
        vue.$message({
            message: `${msg}失败. ${body.msg}`,
            type: "error"
        });
    }
}

export const requestThrown = (vue, errorMsg)=> {
    vue.$message({
        message: `${errorMsg}失败`,
        type: "error"
    });
}