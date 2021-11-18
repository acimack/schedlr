import TextField from '@material-ui/core/TextField'

function DateEntry({label, change, value}) {
    const onChange = (event) => {change(event.target.value)}
    return (
            <TextField
                id="date"
                label={label}
                type="date"
                value={value}
                onChange={onChange}
                inputLabelProps={{ shrink: true }}
            />
    );
}

export default DateEntry;