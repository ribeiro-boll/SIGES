# TO-DO

## Agora

- [ ] Implementar edição das informações do perfil do usuário
- [ ] Alinhar com o front quais campos do perfil poderão ser editados
- [ ] Definir o endpoint e o payload de atualização de perfil
- [ ] Garantir que alterações no perfil reflitam corretamente nas configurações de precificação, quando aplicável

## Próximo passo

- [ ] Criar o módulo de custos fixos mensais
- [ ] Permitir cadastrar o valor do mês anterior para cada custo fixo
- [ ] Separar conceitualmente custos fixos de insumos variáveis no backend
- [ ] Alinhar com o front a interface de cadastro, edição e visualização dos custos fixos
- [ ] Implementar lembrete visual no front para o usuário atualizar os valores mensalmente
- [ ] Definir a regra do lembrete para aparecer a partir de uma data fixa, como o dia 5 de cada mês

## Depois

- [ ] Implementar extrato mensal
- [ ] Definir o conteúdo do extrato mensal, incluindo:
    - [ ] serviços/produtos cadastrados
    - [ ] insumos usados
    - [ ] custos fixos do mês
    - [ ] preços sugeridos
    - [ ] resumo financeiro/gerencial
- [ ] Escolher o formato inicial de exportação:
    - [ ] PDF para visualização
    - [ ] Excel para análise e edição
- [ ] Implementar a geração de PDF ou Excel no backend
- [ ] Alinhar com o front a funcionalidade de exportação do extrato mensal

## Ordem sugerida de implementação

1. Edição de perfil
2. Custos fixos mensais
3. Extrato mensal em PDF ou Excel