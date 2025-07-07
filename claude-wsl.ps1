$currentDir = $PWD.Path
$wslPath = $currentDir -replace '^([A-Z]):', '/mnt/$1' -replace '\\', '/'
$wslPath = $wslPath.ToLower()
wsl -d Ubuntu-24.04 -e bash -c "cd '$wslPath' && source ~/.nvm/nvm.sh && nvm use 24.1.0 && claude sonnet"