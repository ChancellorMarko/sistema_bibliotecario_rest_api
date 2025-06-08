let livroEditando = null

// Declaração das variáveis globais
const API_ENDPOINTS = {
  livros: "/api/livros", // Substitua pela URL real da sua API
}

// Função genérica para exibir alertas
function showAlert(message, type) {
  const alertDiv = document.createElement("div")
  alertDiv.className = `alert alert-${type} alert-dismissible fade show`
  alertDiv.innerHTML = `
        ${message}
        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
            <span aria-hidden="true">&times;</span>
        </button>
    `
  document.getElementById("alert-container").appendChild(alertDiv)

  // Remover o alerta após alguns segundos
  setTimeout(() => {
    alertDiv.remove()
  }, 5000)
}

// Função genérica para fazer requisições à API
async function apiRequest(url, options = {}) {
  try {
    const response = await fetch(url, {
      ...options,
      headers: {
        "Content-Type": "application/json",
        ...options.headers,
      },
    })

    if (!response.ok) {
      const error = await response.json()
      return { success: false, error: error }
    }

    const data = await response.json()
    return { success: true, data: data }
  } catch (error) {
    console.error("Erro na requisição:", error)
    return { success: false, error: { message: "Erro ao comunicar com o servidor" } }
  }
}

document.addEventListener("DOMContentLoaded", () => {
  // Submit do formulário
  document.getElementById("livroForm").addEventListener("submit", salvarLivro)

  // Validação da quantidade disponível
  document.getElementById("quantidadeTotal").addEventListener("input", function () {
    const total = Number.parseInt(this.value) || 0
    const disponivelInput = document.getElementById("quantidadeDisponivel")
    disponivelInput.max = total
    if (Number.parseInt(disponivelInput.value) > total) {
      disponivelInput.value = total
    }
  })
})

async function salvarLivro(event) {
  event.preventDefault()

  const formData = new FormData(event.target)
  const livroData = {
    titulo: formData.get("titulo"),
    autor: formData.get("autor"),
    isbn: formData.get("isbn"),
    categoria: formData.get("categoria"),
    quantidadeTotal: Number.parseInt(formData.get("quantidadeTotal")),
    quantidadeDisponivel: Number.parseInt(formData.get("quantidadeDisponivel")),
  }

  // Validação
  if (livroData.quantidadeDisponivel > livroData.quantidadeTotal) {
    showAlert("Quantidade disponível não pode ser maior que a quantidade total!", "error")
    return
  }

  try {
    let response
    if (livroEditando) {
      // Atualizar livro existente
      response = await apiRequest(`${API_ENDPOINTS.livros}/${livroEditando}`, {
        method: "PUT",
        body: JSON.stringify(livroData),
      })
    } else {
      // Criar novo livro
      response = await apiRequest(API_ENDPOINTS.livros, {
        method: "POST",
        body: JSON.stringify(livroData),
      })
    }

    if (
      handleApiResponse(response, livroEditando ? "Livro atualizado com sucesso!" : "Livro cadastrado com sucesso!")
    ) {
      limparFormulario()

      // Atualizar lista se estiver visível
      if (document.getElementById("listaLivros").style.display !== "none") {
        listarTodosLivros()
      }
    }
  } catch (error) {
    console.error("Erro ao salvar livro:", error)
    showAlert("Erro de conexão com o servidor", "error")
  }
}

async function buscarLivro() {
  const id = document.getElementById("buscarId").value

  if (!id) {
    showAlert("Digite o ID do livro", "error")
    return
  }

  try {
    const response = await apiRequest(`${API_ENDPOINTS.livros}/${id}`)

    if (response.success && response.data) {
      exibirDadosLivro(response.data)
      document.getElementById("listaLivros").style.display = "none"
    } else {
      handleApiResponse(response)
      document.getElementById("resultadoBusca").style.display = "none"
    }
  } catch (error) {
    console.error("Erro ao buscar livro:", error)
    showAlert("Erro de conexão com o servidor", "error")
    document.getElementById("resultadoBusca").style.display = "none"
  }
}

async function listarTodosLivros() {
  try {
    const response = await apiRequest(API_ENDPOINTS.livros)

    if (response.success && response.data) {
      exibirListaLivros(response.data, "Todos os Livros")
      document.getElementById("resultadoBusca").style.display = "none"
    } else {
      handleApiResponse(response)
    }
  } catch (error) {
    console.error("Erro ao listar livros:", error)
    showAlert("Erro de conexão com o servidor", "error")
  }
}

async function listarLivrosDisponiveis() {
  try {
    const response = await apiRequest(`${API_ENDPOINTS.livros}/disponiveis`)

    if (response.success && response.data) {
      exibirListaLivros(response.data, "Livros Disponíveis")
      document.getElementById("resultadoBusca").style.display = "none"
    } else {
      showAlert("Nenhum livro disponível encontrado", "info")
    }
  } catch (error) {
    console.error("Erro ao listar livros disponíveis:", error)
    showAlert("Erro ao carregar livros disponíveis", "error")
  }
}

function exibirListaLivros(livros, titulo) {
  const container = document.getElementById("listaLivros")
  const tabela = document.getElementById("tabelaLivros").getElementsByTagName("tbody")[0]

  // Limpar tabela
  tabela.innerHTML = ""

  // Atualizar título
  container.querySelector("h3").textContent = `📚 ${titulo}`

  // Preencher tabela
  livros.forEach((livro) => {
    const row = tabela.insertRow()
    row.innerHTML = `
            <td>${livro.id}</td>
            <td>${livro.titulo}</td>
            <td>${livro.autor}</td>
            <td>${livro.isbn || "-"}</td>
            <td>${livro.categoria || "-"}</td>
            <td>${livro.quantidadeTotal}</td>
            <td>${livro.quantidadeDisponivel}</td>
            <td class="actions">
                <button class="btn btn-warning btn-sm" onclick="editarLivro(${livro.id})">Editar</button>
                <button class="btn btn-danger btn-sm" onclick="excluirLivro(${livro.id})">Excluir</button>
            </td>
        `
  })

  container.style.display = "block"
}

function exibirDadosLivro(livro) {
  const container = document.getElementById("dadosLivro")
  container.innerHTML = `
        <div class="livro-info">
            <p><strong>ID:</strong> ${livro.id}</p>
            <p><strong>Título:</strong> ${livro.titulo}</p>
            <p><strong>Autor:</strong> ${livro.autor}</p>
            <p><strong>ISBN:</strong> ${livro.isbn || "-"}</p>
            <p><strong>Categoria:</strong> ${livro.categoria || "-"}</p>
            <p><strong>Quantidade Total:</strong> ${livro.quantidadeTotal}</p>
            <p><strong>Quantidade Disponível:</strong> ${livro.quantidadeDisponivel}</p>
            <div class="actions" style="margin-top: 1rem;">
                <button class="btn btn-warning btn-sm" onclick="editarLivro(${livro.id})">Editar</button>
                <button class="btn btn-danger btn-sm" onclick="excluirLivro(${livro.id})">Excluir</button>
            </div>
        </div>
    `

  document.getElementById("resultadoBusca").style.display = "block"
}

function editarLivro(id) {
  buscarLivroParaEdicao(id)
}

async function buscarLivroParaEdicao(id) {
  try {
    const response = await apiRequest(`${API_ENDPOINTS.livros}/${id}`)

    if (response.success && response.data) {
      const livro = response.data

      // Preencher formulário
      document.getElementById("titulo").value = livro.titulo
      document.getElementById("autor").value = livro.autor
      document.getElementById("isbn").value = livro.isbn || ""
      document.getElementById("categoria").value = livro.categoria || ""
      document.getElementById("quantidadeTotal").value = livro.quantidadeTotal
      document.getElementById("quantidadeDisponivel").value = livro.quantidadeDisponivel

      // Alterar estado do formulário para edição
      livroEditando = id
      document.getElementById("btnSubmit").textContent = "Atualizar Livro"
      document.getElementById("btnCancelar").style.display = "inline-block"

      // Scroll para o formulário
      document.getElementById("livroForm").scrollIntoView({ behavior: "smooth" })
    }
  } catch (error) {
    console.error("Erro ao buscar livro para edição:", error)
    showAlert("Erro ao carregar dados do livro", "error")
  }
}

async function excluirLivro(id) {
  if (!confirm("Tem certeza que deseja excluir este livro?")) {
    return
  }

  try {
    const response = await apiRequest(`${API_ENDPOINTS.livros}/${id}`, {
      method: "DELETE",
    })

    if (handleApiResponse(response, "Livro excluído com sucesso!")) {
      // Atualizar lista se estiver visível
      if (document.getElementById("listaLivros").style.display !== "none") {
        listarTodosLivros()
      }

      // Limpar busca individual se for o mesmo livro
      if (document.getElementById("resultadoBusca").style.display !== "none") {
        document.getElementById("resultadoBusca").style.display = "none"
      }

      document.getElementById("buscarId").value = ""
    }
  } catch (error) {
    console.error("Erro ao excluir livro:", error)
    showAlert("Erro de conexão com o servidor", "error")
  }
}

function limparFormulario() {
  document.getElementById("livroForm").reset()
  livroEditando = null
  document.getElementById("btnSubmit").textContent = "Cadastrar Livro"
  document.getElementById("btnCancelar").style.display = "none"
}

function handleApiResponse(response, successMessage = null) {
  if (response.success) {
    if (successMessage) {
      showAlert(successMessage, "success")
    }
    return true
  } else {
    showAlert(response.error?.message || "Ocorreu um erro!", "error")
    return false
  }
}
