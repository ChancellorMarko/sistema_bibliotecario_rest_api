// Configuração da API
const API_BASE_URL = "http://localhost:8080/api"

// Inicialização quando o DOM estiver carregado
document.addEventListener("DOMContentLoaded", () => {
  console.log("✅ Sistema de pagamentos carregado")

  // Definir a data atual no campo de data
  const dataField = document.getElementById("dataPagamento")
  if (dataField) {
    dataField.valueAsDate = new Date()
  }

  // Adicionar event listener para o formulário
  const form = document.getElementById("pagamentoForm")
  if (form) {
    form.addEventListener("submit", handleFormSubmit)
  }

  // Carregar lista inicial de pagamentos
  listarTodosPagamentos()
})

// Função para buscar multa por ID
async function buscarMulta() {
  const multaId = document.getElementById("multaId")?.value

  if (!multaId || multaId.trim() === "") {
    showAlert("Por favor, digite o ID da multa", "error")
    return
  }

  console.log("Buscando multa com ID:", multaId)

  try {
    // Primeiro, verificar se já existe pagamento processado para esta multa
    const responsePagamentos = await fetch(`${API_BASE_URL}/pagamentos`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Accept: "application/json",
      },
    })

    if (responsePagamentos.ok) {
      const resultPagamentos = await responsePagamentos.json()
      const todosPagamentos = Array.isArray(resultPagamentos) ? resultPagamentos : resultPagamentos.data || []

      // Verificar se existe pagamento processado para esta multa
      const pagamentoProcessado = todosPagamentos.find((p) => p.multaId == multaId && p.status === "PROCESSADO")

      if (pagamentoProcessado) {
        showAlert(`Esta multa já foi paga! Pagamento ID: ${pagamentoProcessado.id}`, "warning")
        return
      }
    }

    // Se não foi paga, buscar a multa normalmente
    const response = await fetch(`${API_BASE_URL}/multas/${multaId}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Accept: "application/json",
      },
    })

    if (!response.ok) {
      if (response.status === 404) {
        showAlert(`Multa com ID ${multaId} não encontrada`, "error")
        return
      }
      throw new Error(`HTTP ${response.status}`)
    }

    const result = await response.json()
    console.log("Resposta da API:", result)

    // Verificar se a resposta tem a estrutura esperada
    if (result.success && result.data) {
      const multa = result.data
      preencherDadosMulta(multa)
    } else {
      showAlert("Erro ao buscar multa", "error")
    }
  } catch (error) {
    console.error("Erro detalhado:", error)
    showAlert(`Erro ao buscar multa: ${error.message}`, "error")
  }
}

// Função para preencher os dados da multa no formulário
function preencherDadosMulta(multa) {
  try {
    console.log("Preenchendo dados da multa:", multa)

    // Preencher campos do formulário
    document.getElementById("clienteId").value = multa.clienteId
    document.getElementById("valorPago").value = multa.valor

    // Exibir informações da multa
    const infoMulta = document.getElementById("infoMulta")
    const dadosMulta = document.getElementById("dadosMulta")

    if (infoMulta && dadosMulta) {
      dadosMulta.innerHTML = `
        <p><strong>ID:</strong> ${multa.id}</p>
        <p><strong>Cliente:</strong> ${multa.clienteId}</p>
        <p><strong>Empréstimo:</strong> ${multa.emprestimoId}</p>
        <p><strong>Valor:</strong> R$ ${multa.valor.toFixed(2)}</p>
        <p><strong>Data:</strong> ${formatarData(multa.dataMulta)}</p>
      `
      infoMulta.style.display = "block"
    }

    showAlert("Multa encontrada e dados preenchidos!", "success")
  } catch (error) {
    console.error("Erro ao preencher dados da multa:", error)
    showAlert("Erro ao preencher os dados da multa", "error")
  }
}

// Função para lidar com o envio do formulário
function handleFormSubmit(event) {
  event.preventDefault()

  const formData = {
    multaId: Number.parseInt(document.getElementById("multaId").value),
    clienteId: Number.parseInt(document.getElementById("clienteId").value),
    valorPago: Number.parseFloat(document.getElementById("valorPago").value),
    dataPagamento: document.getElementById("dataPagamento").value,
    metodoPagamento: document.getElementById("metodoPagamento").value,
    status: document.getElementById("status").value,
    observacoes: document.getElementById("observacoes").value || "",
  }

  console.log("Dados do pagamento:", formData)

  // Validar dados obrigatórios
  if (
    !formData.multaId ||
    !formData.clienteId ||
    !formData.valorPago ||
    !formData.dataPagamento ||
    !formData.metodoPagamento
  ) {
    showAlert("Por favor, preencha todos os campos obrigatórios", "error")
    return
  }

  criarPagamento(formData)
}

// Função para criar um novo pagamento
async function criarPagamento(pagamento) {
  // Ajustar os dados para o formato correto do backend
  const pagamentoData = {
    multaId: pagamento.multaId,
    clienteId: pagamento.clienteId,
    valorPago: pagamento.valorPago,
    dataPagamento: pagamento.dataPagamento + "T00:00:00", // Adicionar formato de hora
    metodoPagamento: pagamento.metodoPagamento,
    status: pagamento.status || "PROCESSADO",
    observacoes: pagamento.observacoes || "",
  }

  console.log("Enviando dados para API:", pagamentoData)

  try {
    const response = await fetch(`${API_BASE_URL}/pagamentos`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Accept: "application/json",
      },
      body: JSON.stringify(pagamentoData),
    })

    console.log("Response status:", response.status)

    if (!response.ok) {
      // Tentar ler a resposta de erro
      const errorResponse = await response.json()
      console.log("Error response:", errorResponse)

      let errorMessage = "Erro desconhecido"
      if (errorResponse.error && errorResponse.error.message) {
        errorMessage = errorResponse.error.message
      } else if (errorResponse.message) {
        errorMessage = errorResponse.message
      }

      throw new Error(`${response.status}: ${errorMessage}`)
    }

    const data = await response.json()
    console.log("Pagamento criado:", data)
    showAlert("Pagamento registrado com sucesso!", "success")
    limparFormulario()
    listarTodosPagamentos()
  } catch (error) {
    console.error("Erro detalhado:", error)
    showAlert(`Erro: ${error.message}`, "error")
  }
}

// Função para listar todos os pagamentos (apenas pendentes)
async function listarTodosPagamentos() {
  try {
    const response = await fetch(`${API_BASE_URL}/pagamentos`, {
      method: "GET",
      headers: {
        Accept: "application/json",
      },
    })

    if (!response.ok) {
      throw new Error(`Erro HTTP: ${response.status}`)
    }

    const data = await response.json()
    const todosPagamentos = Array.isArray(data) ? data : data.data || []

    // Filtrar apenas pagamentos pendentes
    const pagamentosPendentes = todosPagamentos.filter((p) => p.status !== "PROCESSADO")

    exibirListaPagamentos(pagamentosPendentes, "Pagamentos Pendentes")
  } catch (error) {
    console.error("Erro ao listar pagamentos:", error)
    showAlert(`Erro ao carregar pagamentos: ${error.message}`, "error")
  }
}

// Nova função para listar TODOS os pagamentos (incluindo processados)
async function listarTodosPagamentosIncluindoProcessados() {
  try {
    const response = await fetch(`${API_BASE_URL}/pagamentos`, {
      method: "GET",
      headers: {
        Accept: "application/json",
      },
    })

    if (!response.ok) {
      throw new Error(`Erro HTTP: ${response.status}`)
    }

    const data = await response.json()
    const pagamentos = Array.isArray(data) ? data : data.data || []
    exibirListaPagamentos(pagamentos, "Todos os Pagamentos (Incluindo Processados)")
  } catch (error) {
    console.error("Erro ao listar pagamentos:", error)
    showAlert(`Erro ao carregar pagamentos: ${error.message}`, "error")
  }
}

// Função para listar pagamentos processados
async function listarPagamentosProcessados() {
  try {
    const response = await fetch(`${API_BASE_URL}/pagamentos`, {
      method: "GET",
      headers: {
        Accept: "application/json",
      },
    })

    if (!response.ok) {
      throw new Error(`Erro ao buscar pagamentos: ${response.status}`)
    }

    const data = await response.json()
    const pagamentos = Array.isArray(data) ? data : data.data || []
    const processados = pagamentos.filter((p) => p.status === "PROCESSADO")
    exibirListaPagamentos(processados, "Pagamentos Processados")
  } catch (error) {
    showAlert(`Erro: ${error.message}`, "error")
  }
}

// Função para listar pagamentos pendentes
async function listarPagamentosPendentes() {
  try {
    const response = await fetch(`${API_BASE_URL}/pagamentos`, {
      method: "GET",
      headers: {
        Accept: "application/json",
      },
    })

    if (!response.ok) {
      throw new Error(`Erro ao buscar pagamentos: ${response.status}`)
    }

    const data = await response.json()
    const pagamentos = Array.isArray(data) ? data : data.data || []
    const pendentes = pagamentos.filter((p) => p.status === "PENDENTE")
    exibirListaPagamentos(pendentes, "Pagamentos Pendentes")
  } catch (error) {
    showAlert(`Erro: ${error.message}`, "error")
  }
}

// Função para buscar pagamento por ID
async function buscarPagamento() {
  const pagamentoId = document.getElementById("buscarPagamentoId").value

  if (!pagamentoId) {
    showAlert("Por favor, informe o ID do pagamento", "error")
    return
  }

  try {
    const response = await fetch(`${API_BASE_URL}/pagamentos/${pagamentoId}`, {
      method: "GET",
      headers: {
        Accept: "application/json",
      },
    })

    if (!response.ok) {
      throw new Error("Pagamento não encontrado")
    }

    const data = await response.json()
    const pagamento = data.data || data
    exibirDetalhesPagamento(pagamento)
  } catch (error) {
    showAlert(`Erro: ${error.message}`, "error")
  }
}

// Função para exibir lista de pagamentos
function exibirListaPagamentos(pagamentos, titulo) {
  const listaPagamentos = document.getElementById("listaPagamentos")
  const tabela = document.getElementById("tabelaPagamentos")
  const tbody = tabela.getElementsByTagName("tbody")[0]

  // Atualizar título
  const h2Element = listaPagamentos.querySelector("h2")
  if (h2Element) {
    h2Element.innerHTML = `<i class="fas fa-list"></i> ${titulo}`
  }

  tbody.innerHTML = ""

  if (!pagamentos || pagamentos.length === 0) {
    tbody.innerHTML = '<tr><td colspan="8" class="text-center">Nenhum pagamento encontrado</td></tr>'
  } else {
    pagamentos.forEach((pagamento) => {
      const row = tbody.insertRow()
      row.innerHTML = `
        <td>${pagamento.id}</td>
        <td>${pagamento.multaId || "N/A"}</td>
        <td>${pagamento.clienteId}</td>
        <td>R$ ${pagamento.valorPago ? pagamento.valorPago.toFixed(2) : "0,00"}</td>
        <td>${formatarData(pagamento.dataPagamento)}</td>
        <td>${formatarMetodoPagamento(pagamento.metodoPagamento)}</td>
        <td><span class="status status-${pagamento.status ? pagamento.status.toLowerCase() : "pendente"}">${pagamento.status || "PENDENTE"}</span></td>
        <td>
          <button class="btn btn-secondary btn-sm" onclick="editarPagamento(${pagamento.id})">
            <i class="fas fa-edit"></i> Editar
          </button>
          <button class="btn btn-danger btn-sm" onclick="excluirPagamento(${pagamento.id})">
            <i class="fas fa-trash"></i> Excluir
          </button>
        </td>
      `
    })
  }

  listaPagamentos.style.display = "block"
  document.getElementById("resultadoBusca").style.display = "none"
}

// Função para exibir detalhes de um pagamento
function exibirDetalhesPagamento(pagamento) {
  const resultadoBusca = document.getElementById("resultadoBusca")
  const dadosPagamento = document.getElementById("dadosPagamento")

  dadosPagamento.innerHTML = `
    <div class="detail-item">
      <h4><i class="fas fa-hashtag"></i> ID do Pagamento</h4>
      <p>${pagamento.id}</p>
    </div>
    <div class="detail-item">
      <h4><i class="fas fa-exclamation-triangle"></i> Multa</h4>
      <p>${pagamento.multaId}</p>
    </div>
    <div class="detail-item">
      <h4><i class="fas fa-user"></i> Cliente</h4>
      <p>${pagamento.clienteId}</p>
    </div>
    <div class="detail-item">
      <h4><i class="fas fa-dollar-sign"></i> Valor Pago</h4>
      <p>R$ ${pagamento.valorPago.toFixed(2)}</p>
    </div>
    <div class="detail-item">
      <h4><i class="fas fa-calendar"></i> Data do Pagamento</h4>
      <p>${formatarData(pagamento.dataPagamento)}</p>
    </div>
    <div class="detail-item">
      <h4><i class="fas fa-credit-card"></i> Método de Pagamento</h4>
      <p>${formatarMetodoPagamento(pagamento.metodoPagamento)}</p>
    </div>
    <div class="detail-item">
      <h4><i class="fas fa-flag"></i> Status</h4>
      <p><span class="status status-${pagamento.status ? pagamento.status.toLowerCase() : "pendente"}">${pagamento.status || "PENDENTE"}</span></p>
    </div>
    <div class="detail-item">
      <h4><i class="fas fa-comment"></i> Observações</h4>
      <p>${pagamento.observacoes || "Nenhuma observação"}</p>
    </div>
  `

  resultadoBusca.style.display = "block"
  document.getElementById("listaPagamentos").style.display = "none"
}

// Função para editar um pagamento
async function editarPagamento(id) {
  try {
    const response = await fetch(`${API_BASE_URL}/pagamentos/${id}`, {
      method: "GET",
      headers: {
        Accept: "application/json",
      },
    })

    if (!response.ok) {
      throw new Error("Pagamento não encontrado")
    }

    const data = await response.json()
    const pagamento = data.data || data

    // Preencher o formulário com os dados do pagamento
    document.getElementById("multaId").value = pagamento.multaId
    document.getElementById("clienteId").value = pagamento.clienteId
    document.getElementById("valorPago").value = pagamento.valorPago
    document.getElementById("dataPagamento").value = pagamento.dataPagamento.split("T")[0]
    document.getElementById("metodoPagamento").value = pagamento.metodoPagamento
    document.getElementById("status").value = pagamento.status
    document.getElementById("observacoes").value = pagamento.observacoes || ""

    // Marcar o formulário para atualização
    document.getElementById("pagamentoForm").dataset.id = id
    document.getElementById("btnSubmit").innerHTML = '<i class="fas fa-save"></i> Atualizar Pagamento'
    document.getElementById("btnCancelar").style.display = "inline-block"

    window.scrollTo(0, 0)
    showAlert("Editando pagamento #" + id, "info")
  } catch (error) {
    showAlert(`Erro: ${error.message}`, "error")
  }
}

// Função para excluir um pagamento
async function excluirPagamento(id) {
  if (confirm("Tem certeza que deseja excluir este pagamento?")) {
    try {
      const response = await fetch(`${API_BASE_URL}/pagamentos/${id}`, {
        method: "DELETE",
        headers: {
          Accept: "application/json",
        },
      })

      if (!response.ok) {
        throw new Error(`Erro ao excluir pagamento: ${response.status}`)
      }

      showAlert("Pagamento excluído com sucesso!", "success")
      listarTodosPagamentos()
      document.getElementById("resultadoBusca").style.display = "none"
    } catch (error) {
      showAlert(`Erro: ${error.message}`, "error")
    }
  }
}

// Função para limpar o formulário
function limparFormulario() {
  document.getElementById("pagamentoForm").reset()
  document.getElementById("pagamentoForm").removeAttribute("data-id")
  document.getElementById("btnSubmit").innerHTML = '<i class="fas fa-save"></i> Registrar Pagamento'
  document.getElementById("btnCancelar").style.display = "none"
  document.getElementById("infoMulta").style.display = "none"
  document.getElementById("dataPagamento").valueAsDate = new Date()
}

// Função para formatar data
function formatarData(dataString) {
  if (!dataString) return "Data não disponível"
  const data = new Date(dataString)
  return data.toLocaleDateString("pt-BR")
}

// Função para formatar método de pagamento
function formatarMetodoPagamento(metodo) {
  const metodos = {
    DINHEIRO: "Dinheiro",
    CARTAO_CREDITO: "Cartão de Crédito",
    CARTAO_DEBITO: "Cartão de Débito",
    PIX: "PIX",
    TRANSFERENCIA: "Transferência Bancária",
  }
  return metodos[metodo] || metodo || "Não informado"
}

// Função para exibir alertas
function showAlert(message, type) {
  let alertContainer = document.getElementById("alertContainer")

  if (!alertContainer) {
    alertContainer = document.createElement("div")
    alertContainer.id = "alertContainer"
    alertContainer.className = "alert-container"
    alertContainer.style.cssText = `
      position: fixed;
      top: 20px;
      right: 20px;
      z-index: 9999;
      max-width: 400px;
    `
    document.body.appendChild(alertContainer)
  }

  const alertDiv = document.createElement("div")
  alertDiv.className = `alert alert-${type}`
  alertDiv.style.cssText = `
    padding: 12px 16px;
    margin-bottom: 10px;
    border-radius: 4px;
    color: white;
    font-weight: 500;
    box-shadow: 0 2px 8px rgba(0,0,0,0.15);
    animation: slideIn 0.3s ease-out;
  `

  // Definir cores baseadas no tipo
  switch (type) {
    case "success":
      alertDiv.style.backgroundColor = "#28a745"
      break
    case "error":
      alertDiv.style.backgroundColor = "#dc3545"
      break
    case "info":
      alertDiv.style.backgroundColor = "#17a2b8"
      break
    case "warning":
      alertDiv.style.backgroundColor = "#ffc107"
      alertDiv.style.color = "#212529"
      break
    default:
      alertDiv.style.backgroundColor = "#6c757d"
  }

  // Adicionar ícone baseado no tipo
  let icon = ""
  switch (type) {
    case "success":
      icon = '<i class="fas fa-check-circle"></i> '
      break
    case "error":
      icon = '<i class="fas fa-exclamation-circle"></i> '
      break
    case "info":
      icon = '<i class="fas fa-info-circle"></i> '
      break
    case "warning":
      icon = '<i class="fas fa-exclamation-triangle"></i> '
      break
    default:
      icon = ""
  }

  alertDiv.innerHTML = icon + message
  alertContainer.appendChild(alertDiv)

  // Remover o alerta após 5 segundos
  setTimeout(() => {
    if (alertDiv.parentNode) {
      alertDiv.remove()
    }
  }, 5000)

  // Adicionar CSS de animação se não existir
  if (!document.getElementById("alertAnimationCSS")) {
    const style = document.createElement("style")
    style.id = "alertAnimationCSS"
    style.textContent = `
      @keyframes slideIn {
        from {
          transform: translateX(100%);
          opacity: 0;
        }
        to {
          transform: translateX(0);
          opacity: 1;
        }
      }
    `
    document.head.appendChild(style)
  }
}
