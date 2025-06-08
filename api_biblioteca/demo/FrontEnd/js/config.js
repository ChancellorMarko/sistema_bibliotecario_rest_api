// Configuração da API
const API_BASE_URL = "http://localhost:8080/api"

// Endpoints da API
const API_ENDPOINTS = {
  clientes: `${API_BASE_URL}/clientes`,
  livros: `${API_BASE_URL}/livros`,
  emprestimos: `${API_BASE_URL}/emprestimos`,
  reservas: `${API_BASE_URL}/reservas`,
  multas: `${API_BASE_URL}/multa`,
  pagamentos: `${API_BASE_URL}/pagamentos`,
}

// Função para fazer requisições HTTP
async function apiRequest(url, options = {}) {
  try {
    const response = await fetch(url, {
      headers: {
        "Content-Type": "application/json",
        ...options.headers,
      },
      ...options,
    })

    const data = await response.json()

    // Verificar se a resposta tem a estrutura ApiResponse
    if (data.hasOwnProperty("success")) {
      return data // Retorna a ApiResponse completa
    }

    // Se não tem a estrutura ApiResponse, assumir que é um sucesso
    if (response.ok) {
      return {
        success: true,
        data: data,
        message: "Operação realizada com sucesso",
      }
    } else {
      // Se não é ok e não tem estrutura ApiResponse, criar ErrorResponse
      return {
        success: false,
        error: {
          message: data.message || `Erro HTTP: ${response.status}`,
          details: data.details || null,
          timestamp: new Date().toISOString(),
        },
      }
    }
  } catch (error) {
    console.error("API Request Error:", error)
    return {
      success: false,
      error: {
        message: "Erro de conexão com o servidor",
        details: error.message,
        timestamp: new Date().toISOString(),
      },
    }
  }
}

// Função para exibir alertas melhorada
function showAlert(message, type = "info") {
  // Remover alertas existentes
  const existingAlerts = document.querySelectorAll(".alert")
  existingAlerts.forEach((alert) => alert.remove())

  const alertDiv = document.createElement("div")
  alertDiv.className = `alert alert-${type}`

  // Adicionar ícones baseados no tipo
  const icons = {
    success: "✅",
    error: "❌",
    warning: "⚠️",
    info: "ℹ️",
  }

  alertDiv.innerHTML = `
    <span class="alert-icon">${icons[type] || icons.info}</span>
    <span class="alert-message">${message}</span>
    <button class="alert-close" onclick="this.parentElement.remove()">×</button>
  `

  // Inserir no topo da página
  const container = document.querySelector("main") || document.body
  container.insertBefore(alertDiv, container.firstChild)

  // Auto-remover após 5 segundos
  setTimeout(() => {
    if (alertDiv.parentNode) {
      alertDiv.remove()
    }
  }, 5000)
}

// Função para lidar com respostas da API
function handleApiResponse(response, successMessage = "Operação realizada com sucesso") {
  if (response.success) {
    if (response.message) {
      showAlert(response.message, "success")
    } else {
      showAlert(successMessage, "success")
    }
    return true
  } else {
    const errorMessage = response.error?.message || "Erro desconhecido"
    showAlert(errorMessage, "error")

    // Log detalhado do erro para debug
    if (response.error?.details) {
      console.error("Detalhes do erro:", response.error.details)
    }
    return false
  }
}

function formatDate(dateString) {
  if (!dateString) return "-"
  const date = new Date(dateString)
  return date.toLocaleDateString("pt-BR")
}

function formatCurrency(value) {
  if (!value) return "R$ 0,00"
  return new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL",
  }).format(value)
}

// Função para validar CPF
function validarCPF(cpf) {
  cpf = cpf.replace(/[^\d]+/g, "")
  if (cpf.length !== 11 || !!cpf.match(/(\d)\1{10}/)) return false

  cpf = cpf.split("").map((el) => +el)
  const rest = (count) =>
    ((cpf.slice(0, count - 12).reduce((soma, el, index) => soma + el * (count - index), 0) * 10) % 11) % 10

  return rest(10) === cpf[9] && rest(11) === cpf[10]
}

// Função para formatar CPF
function formatarCPF(cpf) {
  return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4")
}

// Função para formatar telefone
function formatarTelefone(telefone) {
  const cleaned = telefone.replace(/\D/g, "")
  if (cleaned.length === 11) {
    return cleaned.replace(/(\d{2})(\d{5})(\d{4})/, "($1) $2-$3")
  } else if (cleaned.length === 10) {
    return cleaned.replace(/(\d{2})(\d{4})(\d{4})/, "($1) $2-$3")
  }
  return telefone
}
