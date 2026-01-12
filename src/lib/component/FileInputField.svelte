<script>
	export let data;
</script>

<legend class="fieldset-legend">{data.label}</legend>
<input
	name={data.key}
	type="file"
	class="file-input validator"
	required={data.validate.required}
	placeholder={data.tips}
/>
<div class="validator-hint">
	{@html data.validate.required && '必填 <br />'}
</div>
