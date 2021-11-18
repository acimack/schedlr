import TextField from "@material-ui/core/TextField"

function TextBox({label, change, value}) {
    const onChange = (event) => {change(event.target.value)}
    return (
        <TextField onChange={onChange} id="standard-basic" label={label} value={value} />
    );
}

export default TextBox;
