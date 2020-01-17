<template>
	<div class='createOutput'>
		<el-select class="selectCommon" v-model="configValue" placeholder="请选择输出" size="small" @change="change">
			<el-option v-for="item in configs" :key="item.id" :label="item.name" :value="item.id">
			</el-option>
		</el-select>

		<dynamicForm :configParam="configParam" :handleSubmit="handleOutputSubmit" label="输出配置" />

		<createWriter />

		<br />
		<avue-crud :data="configTableData" :option="tableOption" v-model="obj" @row-del="deleteConfig"></avue-crud>

	</div>

</template>

<script>
import { mapState } from "vuex";
import { getOutputs } from "@/api/process/processParam";
import dynamicForm from "../components/dynamicForm";
import createWriter from "./createWriter";

export default {
	name: "createOutput",
	components: {
		dynamicForm: dynamicForm,
		createWriter: createWriter
	},
	created() {
		getOutputs().then(res => {
			this.configs = res.data.data;
		});
	},
	data() {
		return {
			obj: {},
			configs: [],
			configValue: "",
			form: {},
			configParam: {},
			configTableData: []
		};
	},
	computed: mapState({
		writers: state => state.process.writers,
		writerChange: state => state.process.writerChange,
		tableOption() {
			return {
				title: "新增的写入者：",
				page: false,
				align: "center",
				menuAlign: "center",
				addBtn: false,
				columnBtn: false,
				editBtn: false,
				refreshBtn: false,
				column: [
					{
						label: "code",
						prop: "code"
					},
					{
						label: "id",
						prop: "id"
					},
					{
						label: "配置信息",
						prop: "params"
					}
				]
			};
		}
	}),
	methods: {
		handleOutputSubmit(form, done) {
			this.$store.commit("setOutput", {
				outputId: this.configValue,
				params: form
			});
			done();
			this.$message({
				message: `创建输出成功`,
				type: "success"
			});
		},
		change(item) {
			for (let config of this.configs) {
				if (config.id === item) {
					this.configParam = config.configParam;
				}
			}
		},
		deleteConfig(row) {
			this.$store.commit("removeWriter", row.code);
		}
	},
	watch: {
		writerChange: function() {
			const configTableData = [];
			this.writers.forEach((writer, k) => {
				let paramsJson = "无";
				if (writer && writer.params) {
					paramsJson = JSON.stringify(writer.params, null, 2);
				}
				configTableData.push({
					code: k,
					id: writer.id,
					params: paramsJson
				});
			});
			this.configTableData = configTableData;
		}
	}
};
</script>
<style lang="scss">
@import "~@/styles/process/createOutput.scss";
@import '~@/styles/process/processCommon.scss';
</style>
