import yaml
from tabulate import tabulate

with open('src/plugin.yml', 'r') as file:
    yml_data = yaml.safe_load(file)

commands = yml_data.get('commands', {})
commands_table = []
for command, details in commands.items():
    commands_table.append([
        command,
        details.get('usage', 'N/A'),
        details.get('permission', 'N/A'),
        details.get('description', 'N/A')
    ])

permissions = yml_data.get('permissions', {})
permissions_table = []
for permission, details in permissions.items():
    permissions_table.append([
        permission,
        details.get('description', 'N/A')
    ])

commands_md = f"# Commands\n\n{tabulate(commands_table, headers=['Command', 'Usage', 'Permission', 'Description'], tablefmt='github')}\n\n"
permissions_md = f"# Permissions\n\n{tabulate(permissions_table, headers=['Permission', 'Description'], tablefmt='github')}\n"

with open('COMMANDS.md', 'w') as file:
    file.write(commands_md)
    file.write(permissions_md)

print("Updated commands documentation")
