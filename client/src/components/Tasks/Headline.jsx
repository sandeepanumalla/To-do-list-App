import { Box, Button, Typography } from "@mui/material";
import React, { useContext } from "react";
import { useNavigate } from "react-router";
import TaskContext from "../../context/Context";
import { getAllTasks } from "../../services/UserTasks";
import { LuArrowUpDown } from "react-icons/lu";


export function Headline({HeadlineName, HeadlineIcon}) {
    const navigate = useNavigate();
    const {myState, setMyState} = useContext(TaskContext);

    const handleClick = async () => {
        setMyState("react contest works");
        console.log("clicked sort by");
        const response = await getAllTasks();
        console.log(response)
    }

  return <Box sx={{display: "flex", padding: 1, flexDirection: 'row', color: 'white'
	// paddingTop: "20px",
	// width: "100%",
	// position: "fixed",
	// top: "40px",
	// background: "red",
	// zIndex: 90,
	// backdropFilter: "transparent"
}}>
            <Box sx={{display: 'flex', alignItems: 'center', justifyContent: 'center'}}>
               {HeadlineIcon}
            </Box>
            <Box sx={{paddingLeft: 2, flex: 2}}>
                <Typography variant="h4">{HeadlineName}</Typography>
            </Box>
            <Box  marginLeft={10}>
                <Button sx={{width: '10rem', display: 'flex', justifyContent: 'space-between'}} onClick={handleClick} autoCapitalize={false} variant="contained" color="primary">
                    <Typography autoCapitalize={false} >Sort By</Typography>
                    <LuArrowUpDown />
                </Button>
                <Typography>
                    {myState}
                </Typography>
            </Box>
        </Box>;
}
  