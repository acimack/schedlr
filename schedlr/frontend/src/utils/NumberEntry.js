import TextField from "@material-ui/core/TextField"

function NumberEntry({label, change, value}) {
    const onChange = (event) => {change(event.target.value)}
    return (
        <TextField onChange={onChange} type="number" id="number" label={label} value={value} />
    );
}

export default NumberEntry;
