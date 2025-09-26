const merge = require('deepmerge');
const configBase = require('./sef.config.base');

const getIntlConfig = (packageName, type = "sc", include = [], exclude = []) => {
    if (!packageName) return {
        useIntl: false,
        intl: { type: "sc" }
    }
    let fileFullName = `${packageName}.yml`
    if (type === "sensorsd") fileFullName = `${packageName}.json`
    return {
        useIntl: type === "sensorsd",
        intl: {
            type, // sc, sensorsd. sc使用@sc/intl, sensorsd使用@sensorsd/intl
            // 下面是collect相关配置
            intlPath: `./${fileFullName}`, // 文件的导出位置, ext: { sc: '.yml', sensorsd: '.json' }
            messagePath: './messages', // 生成的临时文件，辅助排查国际化问题
            exclude: ["node_modules", "@sensorsdata/passport", ...exclude], // 收集时跳过的项目
            include: ['node_modules/@sdh', "@cbc/entity-drill-down", "@cbc/entity-metadata-select", ...include], // 收集时要加入的项目，和上面的exclude一起使用
            logPath: '', // 收集到的未进行国际化的文案的路径的输出，目前不太好用，不建议使用
            // crodwin 配置
            projectName: 'SDH-FE', // crodwin中的项目名，默认使用package name
            branchName: 'SDH-FE', // crodwin中的分支名，默认使用package version
            fileName: `${fileFullName}`, // crowdin中的文件名，默认使用 package name 作为文件名, 使用 { sc: '.yml', sensorsd: '.json' } 作为后缀名
            // 下面是upload相关配置
            filePath: `./${fileFullName}`, // 要上传的本地文件的路径， 默认值同 intlPath
            withTranslate: false, // 是否将本地文件中的翻译也上传到crowdin
            // 下面是fetch相关配置
            outputPath: type !== "sensorsd" ? './intl' : `./${fileFullName}`, // 从crowdin拉回来的文件的输出目录/文件, sc填写目录（默认值 ./intl），sensorsd填写文件名（默认值同 intlPath）。
            targetLanguageIds: ['zh-CN', 'zh-TW', 'en'], // 当前要下载的语言种类
        }
    }
};

// eslint-disable-next-line no-unused-vars
module.exports = (config, option = {}) => {
    const { packageName, type, include, exclude } = option
    const intlConfig = getIntlConfig(packageName, type, include, exclude)
    const sefConfig = merge(configBase, merge(intlConfig, config));
    return sefConfig
}

