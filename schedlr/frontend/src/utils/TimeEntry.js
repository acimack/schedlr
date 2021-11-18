import TextField from '@material-ui/core/TextField'

function TimeEntry({label, change, value}) {
    const onChange = (event) => {change(event.target.value)}
    return (
        <div className="TextBox">
            <TextField
                id="time"
                label={label}
                type="time"
                value={value}
                onChange={onChange}
                InputLabelProps={{
                    shrink: true,
                }}
                inputProps={{
                    step: 300, // 5 min
                }}/>
        </div>
    );
}

export default TimeEntry;