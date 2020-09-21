# Command Mapper
a spigot plugin that give ability to change or disable command in specific world <br>
this plugin useful when you're using multiverse to open multiple server 

### Note :
**you need to download [Bukcore](https://github.com/Wireless4024/Bukcore) due to kotlin required**

## Usage
to add
```
usage   : /cmap add <command name> <replace with>

player  : /cmap add kill kick
console : /cmap <worldname> add kill kick
result  : /kill @a  -> /kick @a

or

player  : /cmap add tpo tp @p
console : /cmap <worldname> add tpo tp @p
result  : /tpo @r  -> /tp @p @r
```
to disable command
```
usage   : /cmap add <command name> nothing

player  : /cmap add op nothing
console : /cmap <worldname> add op nothing
result  : /op <player>  will not execute in specific world
```
to remove
```
usage   : /cmap remove <command name>

player  : /cmap remove kill
console : /cmap <worldname> remove kill
result  : /kill @p  -> /kill @p
```
> if you want to apply setting to all world just use * as world name **(console only)** eg. `/cmap * op nothing`

bug or suggestion please create an issue :)

## For developer
you need to download [bukcore-x.x.jar](https://github.com/Wireless4024/Bukcore/releases/) add put it into `libs/bukcore-x.x.jar` to build