# TO-DO

## Agora

- [x] Implementar edição das informações do perfil do usuário
- [x] Alinhar com o front quais campos do perfil poderão ser editados
- [x] Definir o endpoint e o payload de atualização de perfil
- [x] Garantir que alterações no perfil reflitam corretamente nas configurações de precificação, quando aplicável

## Próximo passo

- [x] Criar o módulo de custos fixos mensais
- [x] Permitir cadastrar o valor do mês anterior para cada custo fixo
- [x] Separar conceitualmente custos fixos de insumos variáveis no backend
- [x] Alinhar com o front a interface de cadastro, edição e visualização dos custos fixos
- [x] Implementar lembrete visual no front para o usuário atualizar os valores mensalmente
- [x] Definir a regra do lembrete para aparecer a partir de uma data fixa, como o dia 5 de cada mês

## Depois

- [x] Implementar extrato mensal
- [x] Definir o conteúdo do extrato mensal, incluindo:
    - [x] serviços/produtos cadastrados
    - [x] custos fixos do mês
    - [x] preços sugeridos
    - [x] resumo financeiro/gerencial
- [x] Escolher o formato inicial de exportação:
    - [x] PDF para visualização
    - [ ] Excel para análise e edição
- [x] Implementar a geração de PDF
- [x] Alinhar com o front a funcionalidade de exportação do extrato mensal

## Ordem sugerida de implementação

1. Edição de perfil
2. Custos fixos mensais
3. Extrato mensal em PDF ou Excel
