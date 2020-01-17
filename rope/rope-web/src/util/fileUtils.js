// 文件工具类

/**
 * 得到文件后缀
 * @param filename 文件名称
 * @return {string|null}
 */
export const getFileSuffix = (filename)=> {
    if(filename){
        let index = filename.lastIndexOf(".");
        let suffix = filename.substr(index+1);
        return suffix.toLowerCase();
    }
    return null;
};