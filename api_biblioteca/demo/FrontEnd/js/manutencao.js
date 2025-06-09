// Variáveis locais apenas para este módulo
let livrosDisponiveis = []
let todasManutencoes = []

// Inicialização da página
document.addEventListener("DOMContentLoaded", () => {
  console.log("Página de Manutenção carregada")
  verificarConexaoAPI()
  carregarLivros()
  listarTodasManutencoes()
  configurarFormulario()
  definirDataAtual()
})

// Verificar conexão com a API
function verificarConexaoAPI() {
  fetch("http://localhost:8080/api/livros")
    .then((response) => {
      if (response.ok) {
        console.log("✅ Conexão com API estabelecida")
      } else {
        console.warn("⚠️ API respondeu com status:", response.status)
      }
    })
    .catch((error) => {
      console.error("❌ Erro ao conectar com a API:", error)
      alert("Erro ao conectar com o servidor. Verifique se a API está rodando.")
    })
}

// Definir data atual no campo de data de início
function definirDataAtual() {
  const agora = new Date()
  const dataFormatada = agora.toISOString().slice(0, 16)
  const dataInicioInput = document.getElementById("dataInicio")
  if (dataInicioInput) {
    dataInicioInput.value = dataFormatada
  }
}

// Carregar lista de livros
function carregarLivros() {
  console.log("Carregando livros...")
  fetch("http://localhost:8080/api/livros")
    .then((response) => {
      if (!response.ok) {
        throw new Error(`Erro HTTP: ${response.status}`)
      }
      return response.json()
    })
    .then((response) => {
      // A resposta vem no formato ApiResponse com campo 'data'
      livrosDisponiveis = response.data || []
      preencherSelectLivros()
      console.log("Livros carregados:", livrosDisponiveis.length)
    })
    .catch((error) => {
      console.error("Erro ao carregar livros:", error)
      alert("Erro ao carregar lista de livros")
    })
}

// Preencher selects de livros
function preencherSelectLivros() {
  const selectLivro = document.getElementById("livroId")
  const selectBusca = document.getElementById("buscarLivroId")

  if (!selectLivro || !selectBusca) {
    console.error("Elementos select não encontrados")
    return
  }

  // Limpar opções existentes
  selectLivro.innerHTML = '<option value="">Selecione um livro</option>'
  selectBusca.innerHTML = '<option value="">Todos os livros</option>'

  // Adicionar livros aos selects
  livrosDisponiveis.forEach((livro) => {
    const optionRegistro = document.createElement("option")
    optionRegistro.value = livro.id
    optionRegistro.textContent = `${livro.titulo} - ${livro.autor}`
    selectLivro.appendChild(optionRegistro)

    const optionBusca = document.createElement("option")
    optionBusca.value = livro.id
    optionBusca.textContent = `${livro.titulo} - ${livro.autor}`
    selectBusca.appendChild(optionBusca)
  })
}

// Configurar formulário
function configurarFormulario() {
  const form = document.getElementById("manutencaoForm")
  if (form) {
    form.addEventListener("submit", registrarManutencao)
  }
}

// Registrar nova manutenção
function registrarManutencao(event) {
  event.preventDefault()

  const livroId = document.getElementById("livroId").value
  const descricao = document.getElementById("descricao").value.trim()
  const status = document.getElementById("status").value
  const dataInicio = document.getElementById("dataInicio").value

  // Validações
  if (!livroId) {
    alert("Por favor, selecione um livro")
    return
  }

  if (!descricao) {
    alert("Por favor, descreva a manutenção")
    return
  }

  if (!status) {
    alert("Por favor, selecione o status")
    return
  }

  const formData = {
    livroId: Number.parseInt(livroId),
    descricao: descricao,
    status: status,
    dataInicio: dataInicio,
  }

  console.log("Enviando dados:", formData)

  fetch("http://localhost:8080/api/manutencoes", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(formData),
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error(`Erro HTTP: ${response.status}`)
      }
      return response.json()
    })
    .then((response) => {
      alert("Manutenção registrada com sucesso!")
      document.getElementById("manutencaoForm").reset()
      definirDataAtual()
      listarTodasManutencoes()
    })
    .catch((error) => {
      console.error("Erro ao registrar manutenção:", error)
      alert("Erro ao registrar manutenção. Verifique os dados e tente novamente.")
    })
}

// Listar todas as manutenções em andamento
function listarTodasManutencoes() {
  const resultadosDiv = document.getElementById("resultados")
  if (!resultadosDiv) return

  resultadosDiv.innerHTML = '<p class="loading">Carregando manutenções...</p>'

  fetch("http://localhost:8080/api/manutencoes/andamento")
    .then((response) => {
      if (!response.ok) {
        throw new Error(`Erro HTTP: ${response.status}`)
      }
      return response.json()
    })
    .then((response) => {
      todasManutencoes = response.data || []
      exibirListaManutencoes(todasManutencoes)
    })
    .catch((error) => {
      console.error("Erro ao listar manutenções:", error)
      resultadosDiv.innerHTML = `<p class="error">Erro ao carregar manutenções: ${error.message}</p>`
    })
}

// Buscar manutenções por livro
function buscarPorLivro() {
  const livroId = document.getElementById("buscarLivroId").value

  if (!livroId) {
    listarTodasManutencoes()
    return
  }

  const resultadosDiv = document.getElementById("resultados")
  if (!resultadosDiv) return

  resultadosDiv.innerHTML = '<p class="loading">Buscando manutenções...</p>'

  fetch(`http://localhost:8080/api/manutencoes/livro/${livroId}`)
    .then((response) => {
      if (!response.ok) {
        throw new Error(`Erro HTTP: ${response.status}`)
      }
      return response.json()
    })
    .then((response) => {
      const manutencoes = response.data || []
      exibirListaManutencoes(manutencoes)
    })
    .catch((error) => {
      console.error("Erro ao buscar manutenções:", error)
      resultadosDiv.innerHTML = `<p class="error">Erro ao buscar manutenções: ${error.message}</p>`
    })
}

// Filtrar por status
function filtrarPorStatus() {
  const status = document.getElementById("filtroStatus").value

  if (!status) {
    exibirListaManutencoes(todasManutencoes)
    return
  }

  const manutencoesFiltradas = todasManutencoes.filter((m) => m.status === status)
  exibirListaManutencoes(manutencoesFiltradas)
}

// Exibir lista de manutenções
function exibirListaManutencoes(manutencoes) {
  const resultadosDiv = document.getElementById("resultados")
  if (!resultadosDiv) return

  if (!manutencoes || manutencoes.length === 0) {
    resultadosDiv.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-tools"></i>
                <h3>Nenhuma manutenção encontrada</h3>
                <p>Não há manutenções registradas com os critérios selecionados.</p>
            </div>
        `
    return
  }

  let html = ""

  manutencoes.forEach((manutencao) => {
    // Encontrar informações do livro
    const livro = livrosDisponiveis.find((l) => l.id === manutencao.livroId)
    const tituloLivro = livro ? `${livro.titulo} - ${livro.autor}` : `Livro ID: ${manutencao.livroId}`

    // Formatar datas
    const dataInicio = manutencao.dataInicio ? new Date(manutencao.dataInicio).toLocaleString("pt-BR") : "Não informada"
    const dataFim = manutencao.dataFim ? new Date(manutencao.dataFim).toLocaleString("pt-BR") : "Em andamento"

    // Definir classe de status
    let statusClass = ""
    switch (manutencao.status) {
      case "Em andamento":
        statusClass = "status-em-andamento"
        break
      case "Pendente":
        statusClass = "status-pendente"
        break
      case "Concluída":
        statusClass = "status-concluida"
        break
      default:
        statusClass = ""
    }

    html += `
            <div class="reservation-card">
                <div class="reservation-header">
                    <div class="reservation-id">Manutenção #${manutencao.id}</div>
                    <span class="status-badge ${statusClass}">${manutencao.status || "Não definido"}</span>
                </div>
                
                <div class="reservation-info">
                    <div class="info-item">
                        <span class="info-label">Livro</span>
                        <span class="info-value">${tituloLivro}</span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">Data de Início</span>
                        <span class="info-value">${dataInicio}</span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">Data de Conclusão</span>
                        <span class="info-value">${dataFim}</span>
                    </div>
                </div>
                
                <div class="reservation-description">
                    <strong>Descrição:</strong><br>
                    ${manutencao.descricao || "Sem descrição"}
                </div>
                
                <div class="reservation-actions">
                    ${
                      manutencao.status !== "Concluída"
                        ? `<button class="btn btn-primary" onclick="concluirManutencao(${manutencao.id})">
                            <i class="fas fa-check"></i> Concluir
                        </button>`
                        : `<button class="btn btn-secondary" disabled>
                            <i class="fas fa-check-circle"></i> Já Concluída
                        </button>`
                    }
                </div>
            </div>
        `
  })

  resultadosDiv.innerHTML = html
}

// Concluir manutenção
function concluirManutencao(id) {
  if (!confirm("Tem certeza que deseja marcar esta manutenção como concluída?")) {
    return
  }

  fetch(`http://localhost:8080/api/manutencoes/${id}/concluir`, {
    method: "PATCH",
    headers: {
      "Content-Type": "application/json",
    },
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error(`Erro HTTP: ${response.status}`)
      }
      return response.json()
    })
    .then((response) => {
      alert("Manutenção marcada como concluída!")
      listarTodasManutencoes()
    })
    .catch((error) => {
      console.error("Erro ao concluir manutenção:", error)
      alert("Erro ao concluir manutenção.")
    })
}

// Adicionar estilos para status
document.addEventListener("DOMContentLoaded", () => {
  const style = document.createElement("style")
  style.textContent = `
        .status-badge {
            display: inline-block;
            padding: 5px 10px;
            border-radius: 20px;
            font-size: 0.8rem;
            font-weight: 600;
        }
        
        .status-em-andamento {
            background-color: #fff3cd;
            color: #856404;
        }
        
        .status-pendente {
            background-color: #f8d7da;
            color: #721c24;
        }
        
        .status-concluida {
            background-color: #d4edda;
            color: #155724;
        }
        
        .empty-state {
            text-align: center;
            padding: 40px 20px;
            color: #6c757d;
        }
        
        .empty-state i {
            font-size: 3rem;
            margin-bottom: 15px;
            color: #adb5bd;
        }
        
        .loading {
            text-align: center;
            padding: 20px;
            color: #6c757d;
        }
        
        .error {
            color: #721c24;
            background-color: #f8d7da;
            padding: 15px;
            border-radius: 5px;
            margin: 10px 0;
        }
    `
  document.head.appendChild(style)
})
