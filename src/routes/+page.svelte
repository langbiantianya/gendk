<script>
	let { data } = $props();
	import { onMount } from 'svelte';
	import Go from '$lib/wsam/wasm_exec';
	import InputField from '$lib/component/InputField.svelte';
	import FileInputField from '$lib/component/FileInputField.svelte';
	import SelectField from '$lib/component/SelectField.svelte';
	import CheckboxField from '$lib/component/CheckboxField.svelte';

	const { rulesData = [] } = data;
	let groupSelect = rulesData
		.map((item) => item.data.group)
		.filter((item, index, arr) => arr.indexOf(item) === index);
	console.log(groupSelect);
	let selectedGroup = $state(groupSelect[0]);
	console.log(selectedGroup);
	let groupRulesData = $state(rulesData.filter((item) => selectedGroup === item.data.group));
	console.log(groupRulesData);
	let genformData = $state(groupRulesData[0].data);
	console.log(genformData);
	/**
	 * @type {Go | undefined}
	 */
	let go = undefined;
	/**
	 * @type {WebAssembly.Module | undefined}
	 */
	let mod = undefined;
	/**
	 * @type {WebAssembly.WebAssemblyInstantiatedSource | WebAssembly.Instance | undefined}
	 */
	let inst = undefined;

	async function runWsam() {
		if (!inst) {
			console.error('inst未初始化，无法运行');
			return;
		}
		if (!mod) {
			console.error('mod未初始化，无法运行');
			return;
		}
		if (!go) {
			console.error('Go未初始化，无法运行');
			return;
		}
		await go.run(inst);
		inst = await WebAssembly.instantiate(mod, go.importObject); // reset instance
	}

	function ruleNameSelectChange(e) {
		const rule = JSON.parse(e.target.value);
		genformData = rule;
	}
	function groupSelectChange(e) {
		selectedGroup = e.target.value;
		groupRulesData = rulesData.filter((item) => selectedGroup === item.data.group);
		genformData = groupRulesData[0].data;
	}
	onMount(() => {
		go = new Go();
		if (!WebAssembly.instantiateStreaming) {
			// polyfill
			WebAssembly.instantiateStreaming = async (resp, importObject) => {
				const source = await (await resp).arrayBuffer();
				return await WebAssembly.instantiate(source, importObject);
			};
		}

		WebAssembly.instantiateStreaming(fetch('wasm/app.wasm'), go.importObject)
			.then((result) => {
				mod = result.module;
				inst = result.instance;
				console.log('wsam加载成功');
				runWsam();
			})
			.catch((err) => {
				console.error(err);
			});
	});
</script>

<div class="h-full w-full">
	<div class="mt-4 m-auto w-xs space-y-4">
		<h1 class="text-3xl font-bold">application initializr</h1>
		<div>
			<legend class="fieldset-legend">选择分组</legend>
			<select name="ruleName" onchange={groupSelectChange} class="select mb-8">
				{#each groupSelect as group, index}
					<option value={group} selected={index === 0}>
						{group}
					</option>
				{/each}
			</select>
			<legend class="fieldset-legend">选择模板</legend>
			<select name="ruleName" onchange={ruleNameSelectChange} class="select mb-8">
				{#each groupRulesData as data, index}
					<option value={JSON.stringify(data.data)} selected={index === 0}>
						{data.data?.name || data.filename}
					</option>
				{/each}
			</select>
		</div>
		<form
			class="w-xs"
			action=""
			onsubmit={async (e) => {
				e.preventDefault(); // 阻止默认提交行为
				const formData = new FormData(e.target); // 获取表单数据

				// 将FormData转换为JavaScript对象以便使用
				const data = {};
				for (const [key, value] of formData.entries()) {
					// 判断value 是否为文件,是文件的话以文本方式读取转为字符串
					if (value instanceof File) {
						// 转换为base64编码
						const arrayBuffer = await value.arrayBuffer();
						const base64 = btoa(String.fromCharCode(...new Uint8Array(arrayBuffer)));
						data[key] = { type: 'file', value: base64 };
					} else {
						data[key] = { type: 'text', value: value };
					}
				}
				console.log(data);
				const b64zip = window.genZip(
					JSON.stringify({
						name: genformData.name,
						data
					})
				);
				const a = document.createElement('a');
				a.href = `data:application/zip;base64,${b64zip}`;
				a.download = 'go web基础模板.zip';
				a.click();
				a.remove();
			}}
		>
			<fieldset class="fieldset">
				{#each genformData.components as component}
					{#if component.type === 'text'}
						<InputField data={component} />
					{/if}
					{#if component.type === 'file'}
						<FileInputField data={component} />
					{/if}
					{#if component.type === 'select'}
						<SelectField data={component} />
					{/if}
					{#if component.type === 'checkbox'}
						<CheckboxField data={component} />
					{/if}
				{/each}
				<button class="btn btn-primary" type="submit">Submit form</button>
			</fieldset>
		</form>
	</div>
</div>
