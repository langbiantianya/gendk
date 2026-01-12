<script>
	export let data;
</script>

<legend class="fieldset-legend">{data.label}</legend>
<input
	name={data.key}
	type="text"
	class="input validator"
	required={data.validate.required}
	placeholder={data.tips}
	pattern={data.validate.ValidatorExpr.rule}
/>
<div class="validator-hint">
	{data.validate.required && '必填。'}
	{data.validate.ValidatorExpr.message}
</div>
