import React from 'react';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import ListSubheader from '@material-ui/core/ListSubheader';
import TimerIcon from '@material-ui/icons/Timer';
import HistoryIcon from '@material-ui/icons/History';
import BuildIcon from '@material-ui/icons/Build';
import BarChartIcon from '@material-ui/icons/BarChart';
import SettingsIcon from '@material-ui/icons/Settings';
import AssignmentIcon from '@material-ui/icons/Assignment';
import {Link} from 'react-router-dom';

export const mainListItems = (
  <div>
    <Link to="/">
    <ListItem button>
      <ListItemIcon>
        <TimerIcon />
      </ListItemIcon>
      <ListItemText primary="Realtime" />
    </ListItem>
    </Link>
    <Link to="/historical">
    <ListItem button>
      <ListItemIcon>
        <HistoryIcon />
      </ListItemIcon>
      <ListItemText primary="Historical" />
    </ListItem>
    </Link>
    <ListItem button>
      <ListItemIcon>
        <BarChartIcon />
      </ListItemIcon>
      <ListItemText primary="Bar Replay" />
    </ListItem>
    <ListItem button>
      <ListItemIcon>
        <BuildIcon />
      </ListItemIcon>
      <ListItemText primary="Optimizations" />
    </ListItem>
    <Link to="/settings">
    <ListItem button>
      <ListItemIcon>
        <SettingsIcon />
      </ListItemIcon>
      <ListItemText primary="Settings" />
    </ListItem>
    </Link>
  </div>
);

export const secondaryListItems = (
  <div>
    {/* <ListSubheader inset>Saved reports</ListSubheader>
    <ListItem button>
      <ListItemIcon>
        <AssignmentIcon />
      </ListItemIcon>
      <ListItemText primary="Current month" />
    </ListItem>
    <ListItem button>
      <ListItemIcon>
        <AssignmentIcon />
      </ListItemIcon>
      <ListItemText primary="Last quarter" />
    </ListItem>
    <ListItem button>
      <ListItemIcon>
        <AssignmentIcon />
      </ListItemIcon>
      <ListItemText primary="Year-end sale" />
    </ListItem> */}
  </div>
);