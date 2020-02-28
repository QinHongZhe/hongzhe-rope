#!/bin/sh

buildPath=build
distPath=${buildPath}\dist
pluginPath=${distPath}\plugins
pluginConfigPath=${distPath}\pluginsConfig

cd ../
# 打包
mvn clean install -Dmaven.test.skip=true

# 删除打包的目录
rm -rf ${distPath}

# 创建目录
mkdir ${buildPath}
mkdir ${pluginPath}
mkdir ${pluginConfigPath}


# copy main program and config
cp rope/target/rope-*-exec.jar ${distPath}
cp rope/src/main/resources/application-prod.yml ${distPath}


# copy plugin and config
cp rope-plugins/*/target/*-jar-with-dependencies.jar ${pluginPath}
cp rope-plugins/*/src/main/resources/*.yml ${pluginConfigPath}

cp ${buildPath}/start/start.sh ${distPath}/

cd ${distPath}
mv rope-*-exec.jar rope-start.jar
mv application-prod.yml application.yml
