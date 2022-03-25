# RegionFastReload

The plugin was designed for 2b2t practice servers. Using this plugin, you may set the point A and the point B for the area of your crystal PVP battlefield and clean up all the obsidian blocks and other blocks in range quickly.

**FOR 1.12.2 SERVER ONLY**



## Installation

Just put the plugin jar in your plugins folder.



## Mode

Manual: Using the command /RegionFastReload

Scheduled: You can set a fixed time for cleaning operations in the config



## Config

```yaml
select-a: "<world>;<posX>;<posY>;<posZ>"
select-b: "<world>;<posX2>;<posY2>;<posZ2>"
enable: true
clear-time:
- '<HH>:<mm>'
- '<HH>:<mm>'
- '<HH>:<mm>'
- ...
tip-message: "<...tip message...>
```

You should specify the world you want to operate on first.

You can set multiple time points for cleaning operations.

When the scheduled cleaning operations is going to perform, all the players on the server will receive the title with the message of the `tip-message` .