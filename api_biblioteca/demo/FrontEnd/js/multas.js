// Variável para controlar edição
let multaEditando = null

document.addEventListener("DOMContentLoaded", () => {
  // Definir data padrão como 30 dias a partir de hoje
  const dataVencimento = new Date()
  dataVencimento.setDate(dataVencimento.getDate() + 30)
  document.getElementById("dataVencimento").value = dataVencimento.toISOString().split("T")[0]

  // Submit do formulário
  document.getElementById("multaForm").addEventListener("submit", salvarMulta)

  // Tornar funções acessíveis globalmente
  window.listarTodasMultas = listarTodasMultas
  window.buscarMulta = buscarMulta
  window.editarMulta = editarMulta
  window.excluirMulta = excluirMulta
  window.limparFormulario = limparFormulario
  window.listarTodasMultasIncluindoPagas = listarTodasMultasIncluindoPagas
})

async function salvarMulta(event) {
  event.preventDefault()

  const formData = new FormData(event.target)
  const multaData = {
    clienteId: Number.parseInt(formData.get("clienteId")),
    emprestimoId: Number.parseInt(formData.get("emprestimoId")),
    valor: Number.parseFloat(formData.get("valor")),
    dataMulta: new Date().toISOString(), // Data atual da multa
    motivo: formData.get("motivo") || null,
  }

  try {
    let response
    if (multaEditando) {
      // Atualizar multa existente
      response = await apiRequest(`http://localhost:8080/api/multas/${multaEditando}`, {
        method: "PUT",
        body: JSON.stringify(multaData),
      })
    } else {
      // Criar nova multa
      response = await apiRequest(`http://localhost:8080/api/multas`, {
        method: "POST",
        body: JSON.stringify(multaData),
      })
    }

    if (response) {
      const mensagem = multaEditando ? "Multa atualizada com sucesso!" : `Multa criada com sucesso! ID: ${response.id}`
      showAlert(mensagem, "success")
      limparFormulario()

      // Atualizar lista se estiver visível
      if (document.getElementById("listaMultas").style.display !== "none") {
        listarTodasMultas()
      }
    }
  } catch (error) {
    console.error("Erro ao salvar multa:", error)
    showAlert("Erro ao salvar multa: " + error.message, "error")
  }
}

async function listarTodasMultas() {
  try {
    // Buscar todas as multas
    const multasResponse = await apiRequest(`http://localhost:8080/api/multas`)

    if (multasResponse && Array.isArray(multasResponse)) {
      // Buscar todos os pagamentos processados
      const pagamentosResponse = await apiRequest(`http://localhost:8080/api/pagamentos/status/PROCESSADO`)
      const pagamentosProcessados = pagamentosResponse || []

      // Criar lista de IDs de multas que foram pagas
      const multasPagas = pagamentosProcessados.map((pagamento) => pagamento.multaId)

      // Filtrar apenas multas que NÃO foram pagas
      const multasPendentes = multasResponse.filter((multa) => !multasPagas.includes(multa.id))

      if (multasPendentes.length > 0) {
        exibirListaMultas(multasPendentes, "Multas Pendentes")
        document.getElementById("resultadoBusca").style.display = "none"
      } else {
        showAlert("Nenhuma multa pendente encontrada", "info")
        // Limpar tabela se não houver multas
        const tabela = document.getElementById("tabelaMultas").getElementsByTagName("tbody")[0]
        tabela.innerHTML = ""
        document.getElementById("listaMultas").style.display = "block"
      }
    } else {
      showAlert("Nenhuma multa encontrada", "info")
    }
  } catch (error) {
    console.error("Erro ao listar multas:", error)
    showAlert("Erro ao carregar lista de multas", "error")
  }
}

async function listarTodasMultasIncluindoPagas() {
  try {
    const response = await apiRequest(`http://localhost:8080/api/multas`)

    if (response && Array.isArray(response)) {
      exibirListaMultas(response, "Todas as Multas (Incluindo Pagas)")
      document.getElementById("resultadoBusca").style.display = "none"
    } else {
      showAlert("Nenhuma multa encontrada", "info")
    }
  } catch (error) {
    console.error("Erro ao listar multas:", error)
    showAlert("Erro ao carregar lista de multas", "error")
  }
}

async function buscarMulta() {
  const id = document.getElementById("buscarMultaId").value

  if (!id) {
    showAlert("Digite o ID da multa", "error")
    return
  }

  try {
    const response = await apiRequest(`http://localhost:8080/api/multas/${id}`)

    if (response) {
      exibirDadosMulta(response)
      document.getElementById("listaMultas").style.display = "none"
    } else {
      showAlert("Multa não encontrada", "error")
      document.getElementById("resultadoBusca").style.display = "none"
    }
  } catch (error) {
    console.error("Erro ao buscar multa:", error)
    showAlert("Erro ao buscar multa", "error")
    document.getElementById("resultadoBusca").style.display = "none"
  }
}

function exibirListaMultas(multas, titulo) {
  const container = document.getElementById("listaMultas")
  const tabela = document.getElementById("tabelaMultas").getElementsByTagName("tbody")[0]

  // Limpar tabela
  tabela.innerHTML = ""

  // Atualizar título
  container.querySelector("h3").innerHTML = `<i class="fas fa-exclamation-triangle"></i> ${titulo}`

  // Preencher tabela
  multas.forEach((multa) => {
    const row = tabela.insertRow()
    row.innerHTML = `
        <td>${multa.id}</td>
        <td>${multa.clienteId}</td>
        <td>${multa.emprestimoId}</td>
        <td>${formatCurrency(multa.valor)}</td>
        <td>${formatDate(multa.dataMulta)}</td>
        <td>${multa.motivo || "-"}</td>
        <td class="table-actions">
            <button class="btn btn-warning btn-sm" onclick="editarMulta(${multa.id})">Editar</button>
            <button class="btn btn-danger btn-sm" onclick="excluirMulta(${multa.id})">Excluir</button>
        </td>
    `
  })

  container.style.display = "block"
}

function exibirDadosMulta(multa) {
  const container = document.getElementById("dadosMulta")

  container.innerHTML = `
        <div class="multa-info">
            <p><strong>ID:</strong> ${multa.id}</p>
            <p><strong>Cliente ID:</strong> ${multa.clienteId}</p>
            <p><strong>Empréstimo ID:</strong> ${multa.emprestimoId}</p>
            <p><strong>Valor:</strong> ${formatCurrency(multa.valor)}</p>
            <p><strong>Data da Multa:</strong> ${formatDate(multa.dataMulta)}</p>
            <p><strong>Motivo:</strong> ${multa.motivo || "-"}</p>
            <div class="actions" style="margin-top: 1rem;">
                <button class="btn btn-warning btn-sm" onclick="editarMulta(${multa.id})">Editar</button>
                <button class="btn btn-danger btn-sm" onclick="excluirMulta(${multa.id})">Excluir</button>
            </div>
        </div>
    `

  document.getElementById("resultadoBusca").style.display = "block"
}

function editarMulta(id) {
  buscarMultaParaEdicao(id)
}

async function buscarMultaParaEdicao(id) {
  try {
    const response = await apiRequest(`http://localhost:8080/api/multas/${id}`)

    if (response) {
      const multa = response

      // Preencher formulário
      document.getElementById("clienteId").value = multa.clienteId
      document.getElementById("emprestimoId").value = multa.emprestimoId
      document.getElementById("valor").value = multa.valor
      document.getElementById("dataVencimento").value = multa.dataMulta ? multa.dataMulta.split("T")[0] : ""
      document.getElementById("motivo").value = multa.motivo || ""

      // Alterar estado do formulário para edição
      multaEditando = id
      document.getElementById("btnSubmit").innerHTML = '<i class="fas fa-edit"></i> Atualizar Multa'
      document.getElementById("btnCancelar").style.display = "inline-block"

      // Scroll para o formulário
      document.getElementById("multaForm").scrollIntoView({ behavior: "smooth" })
    }
  } catch (error) {
    console.error("Erro ao buscar multa para edição:", error)
    showAlert("Erro ao carregar dados da multa", "error")
  }
}

async function excluirMulta(id) {
  if (!confirm("Tem certeza que deseja excluir esta multa?")) {
    return
  }

  try {
    // Usar apiRequest em vez de fetch diretamente
    const response = await apiRequest(`http://localhost:8080/api/multas/${id}`, {
      method: "DELETE",
    })

    if (response !== null) {
      showAlert("Multa excluída com sucesso!", "success")

      // Atualizar lista se estiver visível
      if (document.getElementById("listaMultas").style.display !== "none") {
        listarTodasMultas()
      }

      // Limpar busca individual se for a mesma multa
      if (document.getElementById("resultadoBusca").style.display !== "none") {
        document.getElementById("resultadoBusca").style.display = "none"
      }

      document.getElementById("buscarMultaId").value = ""
    } else {
      showAlert("Erro ao excluir multa", "error")
    }
  } catch (error) {
    console.error("Erro ao excluir multa:", error)
    showAlert("Erro ao excluir multa", "error")
  }
}

function getMultaStatusClass(status) {
  switch (status) {
    case "PAGA":
      return "status-paga"
    case "PENDENTE":
      return "status-pendente"
    case "CANCELADA":
      return "status-cancelada"
    default:
      return "status-pendente"
  }
}

function limparFormulario() {
  document.getElementById("multaForm").reset()
  multaEditando = null
  document.getElementById("btnSubmit").textContent = "Criar Multa"
  document.getElementById("btnCancelar").style.display = "none"

  // Redefinir data padrão
  const dataVencimento = new Date()
  dataVencimento.setDate(dataVencimento.getDate() + 30)
  document.getElementById("dataVencimento").value = dataVencimento.toISOString().split("T")[0]
}

// Função para fazer requisições reais à API
async function apiRequest(url, options = {}) {
  try {
    const defaultOptions = {
      headers: {
        "Content-Type": "application/json",
        Accept: "application/json",
      },
    }

    const config = { ...defaultOptions, ...options }

    console.log("Fazendo requisição para:", url, config)

    const response = await fetch(url, config)

    if (!response.ok) {
      if (response.status === 404) {
        return null // Retorna null para 404 (não encontrado)
      }
      throw new Error(`HTTP ${response.status}: ${response.statusText}`)
    }

    // Para DELETE, pode não ter conteúdo
    if (response.status === 204 || options.method === "DELETE") {
      return {} // Retorna objeto vazio para indicar sucesso
    }

    const data = await response.json()

    // Se a resposta tem formato ApiResponse, extrair os dados
    if (data.success !== undefined) {
      return data.success ? data.data : null
    }

    return data
  } catch (error) {
    console.error("Erro na requisição:", error)
    throw error
  }
}

// Função para exibir alertas reais
function showAlert(message, type) {
  // Criar elemento de alerta
  const alertDiv = document.createElement("div")
  alertDiv.className = `alert alert-${type}`
  alertDiv.innerHTML = `
    <i class="fas fa-${getAlertIcon(type)}"></i>
    ${message}
  `

  // Inserir no topo do main-content
  const mainContent = document.querySelector(".main-content")
  mainContent.insertBefore(alertDiv, mainContent.firstChild)

  // Remover após 5 segundos
  setTimeout(() => {
    if (alertDiv.parentNode) {
      alertDiv.parentNode.removeChild(alertDiv)
    }
  }, 5000)
}

function getAlertIcon(type) {
  switch (type) {
    case "success":
      return "check-circle"
    case "error":
      return "exclamation-circle"
    case "warning":
      return "exclamation-triangle"
    case "info":
      return "info-circle"
    default:
      return "info-circle"
  }
}

// Função para formatar moeda
function formatCurrency(value) {
  return new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL",
  }).format(value)
}

// Função para formatar data
function formatDate(dateString) {
  if (!dateString) return "-"
  const date = new Date(dateString)
  return date.toLocaleDateString("pt-BR")
}
