import os
from pathlib import Path

# Configurações
project_root = r"C:\Users\jasso\OneDrive\Documentos\Estudos\Projeto NTT\config-service"
output_file = "config-service-complete.md"

# Extensões que queremos documentar
extensions = ['.java', '.xml', '.yml', '.yaml', '.properties']

# Diretórios para ignorar
ignore_dirs = {'target', '.git', '.idea', 'node_modules', '.mvn', 'bin', 'build'}

def should_process_file(filename):
    """Verifica se o arquivo deve ser processado"""
    return any(filename.endswith(ext) for ext in extensions)

def get_language_tag(filename):
    """Retorna a tag de linguagem para markdown"""
    if filename.endswith('.java'):
        return 'java'
    elif filename.endswith('.xml'):
        return 'xml'
    elif filename.endswith(('.yml', '.yaml')):
        return 'yaml'
    elif filename.endswith('.properties'):
        return 'properties'
    return 'text'

# Abre o arquivo de saída
with open(output_file, 'w', encoding='utf-8') as out:
    out.write("# Config Service - Documentação Completa\n\n")
    out.write("Gerado automaticamente do projeto\n\n")
    
    # ===== ESTRUTURA DE PASTAS =====
    out.write("## 📁 Estrutura de Pastas\n\n```\n")
    
    for root, dirs, files in os.walk(project_root):
        # Remove diretórios ignorados
        dirs[:] = [d for d in dirs if d not in ignore_dirs]
        
        # Calcula nível de indentação
        level = root.replace(project_root, '').count(os.sep)
        indent = '│   ' * level
        folder_name = os.path.basename(root) if root != project_root else 'config-service'
        out.write(f'{indent}├── {folder_name}/\n')
        
        # Lista arquivos relevantes
        sub_indent = '│   ' * (level + 1)
        for file in sorted(files):
            if should_process_file(file):
                out.write(f'{sub_indent}├── {file}\n')
    
    out.write("```\n\n")
    
    # ===== CONTEÚDO DOS ARQUIVOS =====
    out.write("## 📄 Código Fonte\n\n")
    
    # Organiza arquivos por tipo
    file_groups = {
        'Configuração': [],
        'Models/Entities': [],
        'Repositories': [],
        'Services': [],
        'Controllers': [],
        'DTOs': [],
        'Events': [],
        'Kafka': [],
        'Outros': []
    }
    
    for root, dirs, files in os.walk(project_root):
        dirs[:] = [d for d in dirs if d not in ignore_dirs]
        
        for file in sorted(files):
            if should_process_file(file):
                file_path = os.path.join(root, file)
                relative_path = os.path.relpath(file_path, project_root)
                
                # Categoriza o arquivo
                if 'pom.xml' in file or 'application' in file:
                    file_groups['Configuração'].append((relative_path, file_path))
                elif 'model' in root.lower() or 'entity' in root.lower():
                    file_groups['Models/Entities'].append((relative_path, file_path))
                elif 'repository' in root.lower():
                    file_groups['Repositories'].append((relative_path, file_path))
                elif 'service' in root.lower() and 'kafka' not in root.lower():
                    file_groups['Services'].append((relative_path, file_path))
                elif 'controller' in root.lower():
                    file_groups['Controllers'].append((relative_path, file_path))
                elif 'dto' in root.lower():
                    file_groups['DTOs'].append((relative_path, file_path))
                elif 'event' in root.lower():
                    file_groups['Events'].append((relative_path, file_path))
                elif 'kafka' in root.lower() or 'producer' in root.lower():
                    file_groups['Kafka'].append((relative_path, file_path))
                else:
                    file_groups['Outros'].append((relative_path, file_path))
    
    # Escreve arquivos organizados por categoria
    for category, files in file_groups.items():
        if files:
            out.write(f"### {category}\n\n")
            
            for relative_path, file_path in files:
                out.write(f"#### `{relative_path}`\n\n")
                
                lang_tag = get_language_tag(file_path)
                out.write(f"```{lang_tag}\n")
                
                try:
                    with open(file_path, 'r', encoding='utf-8') as f:
                        content = f.read()
                        out.write(content)
                        if not content.endswith('\n'):
                            out.write('\n')
                except Exception as e:
                    out.write(f"// Erro ao ler arquivo: {e}\n")
                
                out.write("```\n\n")
            
            out.write("---\n\n")

print(f"✅ Documentação gerada com sucesso!")
print(f"📄 Arquivo: {output_file}")
print(f"📍 Localização: {os.path.abspath(output_file)}")