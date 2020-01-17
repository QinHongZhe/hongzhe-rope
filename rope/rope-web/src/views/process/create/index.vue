<template>
	<basic-container class='baseProcess'>
		<el-steps :active="active" finish-status="success" align-center>
			<el-step title="基础配置"></el-step>
			<el-step title="输入"></el-step>
			<el-step title="数据处理"></el-step>
			<el-step title="输出"></el-step>
		</el-steps>

		<br /><br />
		<div>
			<baseProcess v-show="active === 0" />
			<createInput v-show="active === 1" />
			<createDataHandler v-show="active === 2" />
			<createOutput v-show="active === 3" />
		</div>

		<el-button style="margin: 12px;margin-left:0;" @click="next">下一步</el-button>
		<el-button style="margin: 12px;margin-left:0;" @click="pre" id="pre">上一步</el-button>
		<el-button type="primary" v-if="active === 3" @click="saveProcess" style="margin: 20px;margin-left:0;">
			保存流程
		</el-button>
	</basic-container>
</template>

<script>
import baseProcess from "./baseProcess";
import createInput from "./input/createInput";
import createDataHandler from "./handler/createDataHandler";
import createOutput from "./ouput/createOutput";

export default {
	name: "createProcess",
	components: {
		baseProcess: baseProcess,
		createInput: createInput,
		createDataHandler: createDataHandler,
		createOutput: createOutput
	},
	data() {
		return {
			active: 0
		};
	},

	methods: {
		next() {
			if (this.active < 3) {
				this.active++;
			}
		},
		pre() {
			if (this.active > 0) {
				this.active--;
			}
		},
		saveProcess() {
			// 提交创建流程
			const promise = this.$store.dispatch("createProcess");
			promise.then(result => {
				if (result === true) {
					this.$message({
						message: `创建流程成功`,
						type: "success"
					});
				} else {
					this.$message({
						message: `${result}`,
						type: "error"
					});
				}
			});
		}
	}
};
</script>

<style scoped>
</style>