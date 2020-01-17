<template>
	<div>
		<basic-container>
			<avue-data-cardtext :option="throughputOption"></avue-data-cardtext>
			<avue-data-box :option="processOption"></avue-data-box>
		</basic-container>
	</div>
</template>

<script>
import { countProcess } from "@/api/process/processManage";
import { overviewMetrics } from "@/api/process/processMetrics";

const counKey = 'count.1-sec-rate';
const byteKey = 'byte.1-sec-rate';

const defaultOverviewMetrics = {
	input: {
		size: 0,
		byteSize: "0 / B"
	},
	output: {
		size: 0,
		byteSize: "0 / B"
	},
	writers: {
		size: 0,
		byteSize: "0 / B"
	}
};

export default {
	name: "wel",
	data() {
		return {
			tableOption: {
				border: true,
				index: true,
				expand: true,
				stripe: true,
				selection: true,
				page: false,
				menuBtn: true,
				menuAlign: "center",
				align: "center"
			},
			processCount: {
				count: 0,
				startCount: 0,
				stopCount: 0
			},
			overviewMetrics: defaultOverviewMetrics,
			timer: null
		};
	},
	mounted() {

	},
	created() {
		countProcess().then(res => {
			this.processCount = res.data.data;
		});
		if (!this.timer) {
			this.timer = setInterval(this.getOverviewMetrics, 1000);
		}
	},
	beforeDestroy() {
		if (this.timer) {
			//关闭
			clearInterval(this.timer);
		}
	},
	computed: {
		throughputOption() {
			return {
				data: [
					{
						title: '输入',
						color: 'rgb(49, 180, 141)',
						icon: 'el-icon-download',
						content: `速率：${this.overviewMetrics.input.size} / 条`,
						name: `大小：${this.overviewMetrics.input.byteSize}`,
					},
					{
						title: '输出',
						color: 'rgb(56, 161, 242)',
						icon: 'el-icon-upload2',
						content: `速率：${this.overviewMetrics.output.size} / 条`,
						name: `大小：${this.overviewMetrics.output.byteSize}`,
					},
					{
						title: '写入',
						color: 'rgb(117, 56, 199)',
						icon: 'el-icon-finished',
						content: `速率：${this.overviewMetrics.writers.size} / 条`,
						name: `大小：${this.overviewMetrics.writers.byteSize}`,
					}
				]
			}
		},
		processOption() {
			const tag = this;
			return {
				data: [
					{
						title: "总流程数",
						count: this.processCount.count,
						icon: "icon-cuowu",
						color: "rgb(49, 180, 141)",
						click: function() {
							tag.toProcessPage();
						}
					},
					{
						title: "启动流程数",
						count: this.processCount.startCount,
						icon: "icon-shujuzhanshi2",
						color: "rgb(56, 161, 242)",
						click: function() {
							tag.toProcessPage();
						}
					},
					{
						title: "停止流程数",
						count: this.processCount.stopCount,
						icon: "icon-jiaoseguanli",
						color: "rgb(117, 56, 199)",
						click: function() {
							tag.toProcessPage();
						}
					}
				]
			};
		}
	},
	methods: {
		toProcessPage() {
			this.$router.push({
				path: "/process/manage"
			});
		},
		getOverviewMetrics(){
			overviewMetrics().then(res => {
				if(!res || !res.data || !res.data.data){
					this.overviewMetrics = defaultOverviewMetrics;
					return;
				}
				const data = res.data.data;
				let overviewMetrics = {};
				const input = data.input;
				if(input){
					overviewMetrics.input = this.getMetric(input);
				} else {
					overviewMetrics.input = {
						size: 0,
						byteSize: 0
					}
				}
				const output = data.output;
				if(output){
					overviewMetrics.output = this.getMetric(output);
				} else {
					overviewMetrics.output = {
						size: 0,
						byteSize: 0
					}
				}
				const writers = data.writers;
				if(writers){
					overviewMetrics.writers = this.getMetric(writers);
				} else {
					overviewMetrics.writers = {
						size: 0,
						byteSize: 0
					}
				}
				this.overviewMetrics = overviewMetrics;
			}, error => {
				console.error(error);
				this.overviewMetrics = defaultOverviewMetrics;
			});
		},
		getMetric(data){
			let size = 0;
			let byteSize = 0;
			if(data){
				data.forEach(item=>{
					const value = item.metric.value;
					if(item.name === counKey){
						size = size + value;
					} else if(item.name === byteKey){
						byteSize = byteSize + value;
					}
				});
			}
			return {
				size: size,
				byteSize: this.bytesFormat(byteSize)
			}
		},
		bytesFormat(bytes) {
			if(null == bytes|| bytes === '' || bytes === 0){
				return "0 / B";
			}
			const unitArr = new Array("B","KB","MB","GB","TB","PB","EB","ZB","YB");
			let index = 0;
			const srcSize = parseFloat(bytes);
			index=Math.floor(Math.log(srcSize)/Math.log(1024));
			let size = srcSize/Math.pow(1024,index);
			size = size.toFixed(2);//保留的小数位数
			return size + " / " +unitArr[index];
		}
	},
};
</script>

<style  lang="scss">
.data-icons .item {
	margin: 0;
}
.wel-info {
	padding: 26px 0;
	.img-border {
		width: 64px;
		height: 65px;
		position: relative;
		vertical-align: middle;
		display: inline-block;
	}
	.img-v {
		position: absolute;
		bottom: -2px;
		right: -2px;
		width: 22px;
		height: 22px;
	}
	.img {
		border-radius: 5px;
		width: 64px;
		height: 64px;
		display: inline-block;
		overflow: hidden;
		img {
			display: block;
			max-width: none;
			height: 64px;
			opacity: 1;
			width: 64px;
			margin-left: 0px;
			margin-top: 0px;
		}
	}
	.user {
		margin-left: 20px;
		display: inline-block;
		color: rgb(153, 153, 153);
		vertical-align: middle;
	}
	.user-title {
		font-size: 18px;
		color: rgb(102, 102, 102);
		margin-right: 5px;
		display: inline-block;
		max-width: 200px;
		white-space: nowrap;
		text-overflow: ellipsis;
		overflow: hidden;
	}
	.user-subtitle {
		display: inline-block;
		width: 40px;
		height: 16px;
		line-height: 16px;
		border-radius: 2px;
		padding: 0px 5px;
		margin-left: 10px;
		font-size: 12px;
		text-align: center;
		color: rgb(255, 44, 84);
		background-color: rgb(255, 242, 244);
		white-space: nowrap;
	}
	.user-item {
		font-size: 12px;
		line-height: 20px;
	}
}
</style>
