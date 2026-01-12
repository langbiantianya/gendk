<script>
	export let data;
</script>

<legend class="fieldset-legend">{data.label}</legend>

<select
	name={data.key}
	class="select validator"
	required={data.validate.required}
	placeholder={data.tips}
>
	<option disabled selected value="">无</option>
	{#each data.options as option}
		<option value={option.value} selected={option.default}>{option.lable}</option>
	{/each}
</select>

<div  class="validator-hint">
	{@html data.validate.required && '必填 <br />'}
</div >
